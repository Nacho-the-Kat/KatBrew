package com.katbrew.workflows.tasks.nfts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.Holder;
import com.katbrew.entities.jooq.db.tables.pojos.LastUpdate;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollection;
import com.katbrew.entities.jooq.db.tables.pojos.NftTransaction;
import com.katbrew.helper.KatBrewHelper;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.pojos.NftTransactionExternal;
import com.katbrew.services.tables.HolderService;
import com.katbrew.services.tables.LastUpdateService;
import com.katbrew.services.tables.NFTCollectionService;
import com.katbrew.services.tables.NFTTransactionService;
import com.katbrew.workflows.helper.ParsingResponse;
import com.katbrew.workflows.helper.ParsingResponsePagedNFT;
import com.katbrew.workflows.tasks.transactions.GenerateLastUpdateTransactionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchNFTTransactions implements JavaDelegate {
    private final ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    @Value("${data.fetchNFT.transactions.baseUrl}")
    private String fetchBaseUrl;
    private final static int limit = 15000;
    private final GenerateLastUpdateTransactionsService generateLastUpdateTransactionsService;
    private final LastUpdateService lastUpdateService;
    private final NFTTransactionService nftTransactionService;
    private final NFTCollectionService nftCollectionService;
    private final HolderService holderService;
    public final DSLContext dsl;
    public final com.katbrew.entities.jooq.db.tables.NftTransaction transactionTable = Tables.NFT_TRANSACTION;

    private static String getCursor(ParsingResponsePagedNFT<List<NftTransactionExternal>> entity) {
        return entity.getNext();
    }

    @Override
    public void execute(DelegateExecution execution) throws JsonProcessingException {
        log.info("Starting the transaction sync:" + LocalDateTime.now());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            final Map<String, BigInteger> tokenMap = nftCollectionService.findAll().stream().collect(Collectors.toMap(NftCollection::getTick, NftCollection::getId));

            final LastUpdate lastCursor = lastUpdateService.findByIdentifier("fetchNFTTransactionsLastCursor");

            final ConcurrentMap<String, BigInteger> holderMap = holderService.getAddressIdMap();
            final ParameterizedTypeReference<ParsingResponsePagedNFT<List<NftTransactionExternal>>> reference = new ParameterizedTypeReference<>() {
            };

            final KatBrewHelper<ParsingResponsePagedNFT<List<NftTransactionExternal>>, NftTransactionExternal> helper = new KatBrewHelper<>();

            try {

                log.info("Start fetching the NFT Transactions, last cursor: " + (lastCursor != null ? lastCursor.getData() : "not exists"));

                final List<NftTransactionExternal> result = helper.fetchPaginated(
                        fetchBaseUrl,
                        lastCursor != null ? lastCursor.getData() : null,
                        false,
                        true,
                        "?offset=",
                        reference,
                        FetchNFTTransactions::getCursor,
                        ParsingResponse::getResult,
                        limit,
                        internResult -> prepareData(internResult, tokenMap, holderMap, true)
                );

                if (result != null) {
                    prepareData(result, tokenMap, holderMap, false);
                    generateLastUpdateTransactionsService.execute();
                } else {
                    log.error("no transactions were loaded");
                }

                log.info("Finished the nft transaction fetching: " + LocalDateTime.now());
            } catch (Exception e) {
                log.info(e.getMessage());
            }
            releaseTask();
            log.info("Finished the nft transaction sync:" + LocalDateTime.now());
            executorService.shutdown();
        });
    }

    public Void prepareData(
            final List<NftTransactionExternal> result,
            final Map<String, BigInteger> tokenIdMap,
            final ConcurrentMap<String, BigInteger> holderMap,
            final Boolean isSafetySave
    ) {

        List<NftTransaction> transactions = result.stream().map(single -> {
            final NftTransaction transaction = mapper.convertValue(single, NftTransaction.class);
            BigInteger deployer = holderMap.get(single.getDeployer());
            BigInteger collectionId = tokenIdMap.get(single.getTick());
            if (deployer == null) {
                final Holder newHolder = new Holder();
                newHolder.setAddress(single.getDeployer());
                final Holder created = holderService.insert(newHolder);
                deployer = created.getId();
                holderMap.put(created.getAddress(), created.getId());
            }

            BigInteger to = holderMap.get(single.getTo());
            if (to == null) {
                final Holder newHolder = new Holder();
                newHolder.setAddress(single.getTo());
                final Holder created = holderService.insert(newHolder);
                to = created.getId();
                holderMap.put(created.getAddress(), created.getId());
            }
            transaction.setDeployer(deployer);
            transaction.setToAddress(to);
            transaction.setFkNftCollection(collectionId);
            if (collectionId == null) {
                if (single.getOpError().isEmpty()) {
                    NftCollection collection = new NftCollection();
                    collection.setBuri(single.getOpData().getBuri());
                    collection.setTick(single.getTick());
                    collection = nftCollectionService.insert(collection);
                    tokenIdMap.put(collection.getTick(), collection.getId());
                } else {
                    transaction.setTransactionTick(single.getTick());
                }
            }
            return transaction;
        }).toList();
        updateAndInsertAll(transactions, isSafetySave);
        return null;
    }


    private void updateAndInsertAll(final List<NftTransaction> transactions, final Boolean isSafetySave) {

        final Map<String, BigInteger> dbEntries = dsl
                .select(transactionTable.ID, transactionTable.TX_ID_REV)
                .from(transactionTable)
                .where(List.of(
                                transactionTable.TX_ID_REV.in(transactions.stream().map(NftTransaction::getTxIdRev).toList())
                        )
                )
                .fetch()
                .intoMaps()
                .stream()
                .collect(Collectors.toMap(single -> (String) single.get("tx_id_rev"), single -> new BigInteger(String.valueOf(single.get("id")))));

        transactions.sort(Comparator.comparing(NftTransaction::getOpScore));

        final List<NftTransaction> toUpdate = new ArrayList<>();
        final List<NftTransaction> toInsert = new ArrayList<>();

        transactions.forEach(transaction -> {
            final BigInteger dbEntryId = dbEntries.get(transaction.getTxIdRev());
            if (dbEntryId != null) {
                transaction.setId(dbEntryId);
                toUpdate.add(transaction);
            } else {
                toInsert.add(transaction);
            }
        });

        nftTransactionService.batchUpdate(toUpdate);
        nftTransactionService.batchInsertVoid(toInsert);

        final LastUpdate lastSafetySave = lastUpdateService.findByIdentifier("fetchNFTTransactionsSafetySave");
        if (isSafetySave) {
            lastSafetySave.setData(transactions.get(transactions.size() - 1).getOpScore().toString());
        } else {
            lastSafetySave.setData(null);
        }
        lastUpdateService.update(lastSafetySave);
    }

    public void releaseTask() {
        LastUpdate lastUpdate = lastUpdateService.findByIdentifier("fetchNFTTransactions");
        if (lastUpdate != null) {
            lastUpdate.setData(null);
            lastUpdateService.update(lastUpdate);
        }
    }
}
