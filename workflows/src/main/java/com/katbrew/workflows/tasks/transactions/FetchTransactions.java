package com.katbrew.workflows.tasks.transactions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.Holder;
import com.katbrew.entities.jooq.db.tables.pojos.LastUpdate;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.entities.jooq.db.tables.pojos.Transaction;
import com.katbrew.helper.KatBrewHelper;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.pojos.TransactionExternal;
import com.katbrew.services.tables.HolderService;
import com.katbrew.services.tables.LastUpdateService;
import com.katbrew.services.tables.TokenService;
import com.katbrew.services.tables.TransactionService;
import com.katbrew.workflows.helper.ParsingResponse;
import com.katbrew.workflows.helper.ParsingResponsePaged;
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
public class FetchTransactions implements JavaDelegate {
    private final ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    @Value("${data.fetchTokenBaseUrl}")
    private String fetchBaseUrl;
    private final static int limit = 15000;
    private final GenerateLastUpdateTransactionsService generateLastUpdateTransactionsService;
    private final LastUpdateService lastUpdateService;
    private final TransactionService transactionService;
    private final TokenService tokenService;
    private final HolderService holderService;
    public final DSLContext dsl;
    public final com.katbrew.entities.jooq.db.tables.Transaction transactionTable = Tables.TRANSACTION;

    private static String getCursor(ParsingResponsePaged<List<TransactionExternal>> entity) {
        if (entity.getPagination().getHasMore()) {
            entity.getResult().sort(Comparator.comparing(TransactionExternal::getOpScore).reversed());
            return entity.getResult().get(entity.getResult().size() - 1).getOpScore().toString();
        }
        return null;
    }

    @Override
    public void execute(DelegateExecution execution) throws JsonProcessingException {
        log.info("Starting the transaction sync:" + LocalDateTime.now());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            final Map<String, Integer> tokenMap = tokenService.findAll().stream().collect(Collectors.toMap(Token::getTick, Token::getId));

            final LastUpdate lastCursor = lastUpdateService.findByIdentifier("fetchTransactionsLastCursor");

            final ConcurrentMap<String, BigInteger> holderMap = holderService.getAddressIdMap();
            final ParameterizedTypeReference<ParsingResponsePaged<List<TransactionExternal>>> reference = new ParameterizedTypeReference<>() {
            };

            final KatBrewHelper<ParsingResponsePaged<List<TransactionExternal>>, TransactionExternal> helper = new KatBrewHelper<>();

            try {

                log.info("Start fetching the Transactions, last cursor: " + (lastCursor != null ? lastCursor.getData() : "nicht vorhanden"));

                String uri = fetchBaseUrl + "/transactions";

                //todo uncomment after sync
//                if (lastCursor != null) {
//                    //if we fetched initially, no need for pageSize over 200
//                    uri += "&pageSize=" + 200;
//                } else {
                uri += "?pageSize=" + 5000;
//                    log.info("no last cursor found, starting full fetch");
//                }
                final List<TransactionExternal> result = helper.fetchPaginated(
                        uri,
                        lastCursor != null ? lastCursor.getData() : null,
                        true,
                        "&lastScore=",
                        reference,
                        FetchTransactions::getCursor,
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

                log.info("Finished the transaction fetching: " + LocalDateTime.now());
            } catch (Exception e) {
                log.info(e.getMessage());
            }
            releaseTask();
            log.info("Finished the transaction sync:" + LocalDateTime.now());
            executorService.shutdown();
        });
    }

    public Void prepareData(
            final List<TransactionExternal> result,
            final Map<String, Integer> tokenIdMap,
            final ConcurrentMap<String, BigInteger> holderMap,
            final Boolean isSafetySave
    ) {

        result.forEach(single -> {
            BigInteger from = holderMap.get(single.getFrom());
            Integer tokenId = tokenIdMap.get(single.getTick());
            if (from == null) {
                final Holder newHolder = new Holder();
                newHolder.setAddress(single.getFrom());
                final Holder created = holderService.insert(newHolder);
                from = created.getId();
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
            single.setFromAddress(from);
            single.setToAddress(to);
            single.setFkToken(tokenId);
            if (tokenId == null) {
                single.setTransactionTick(single.getTick());
            }
        });
        final List<Transaction> transactions = mapper.convertValue(result, new TypeReference<>() {
        });
        updateAndInsertAll(transactions, isSafetySave);
        return null;
    }


    private void updateAndInsertAll(final List<Transaction> transactions, final Boolean isSafetySave) {

        final Map<String, BigInteger> dbEntries = dsl
                .select(transactionTable.ID, transactionTable.HASH_REV)
                .from(transactionTable)
                .where(List.of(
                                transactionTable.HASH_REV.in(transactions.stream().map(Transaction::getHashRev).toList())
                        )
                )
                .fetch()
                .intoMaps()
                .stream()
                .collect(Collectors.toMap(single -> (String) single.get("hash_rev"), single -> new BigInteger(String.valueOf(single.get("id")))));

        transactions.sort(Comparator.comparing(Transaction::getOpScore));

        final List<Transaction> toUpdate = new ArrayList<>();
        final List<Transaction> toInsert = new ArrayList<>();

        transactions.forEach(transaction -> {
            final BigInteger dbEntryId = dbEntries.get(transaction.getHashRev());
            if (dbEntryId != null) {
                transaction.setId(dbEntryId);
                toUpdate.add(transaction);
            } else {
                toInsert.add(transaction);
            }
        });

        transactionService.batchUpdate(toUpdate);
        transactionService.batchInsertVoid(toInsert);

        //we update/add the transaction count, no full fetch needed
        final List<Token> tokens = tokenService.findAll();
        tokens.forEach(single -> {
            final int amount = toInsert.stream().filter(singleTransaction -> singleTransaction.getFkToken().equals(single.getId())).toList().size();
            single.setTransferTotal(
                    single.getTransferTotal().add(BigInteger.valueOf(amount))
            );
        });
        tokenService.update(tokens);

        final LastUpdate lastSafetySave = lastUpdateService.findByIdentifier("fetchTransactionsSafetySave");
        if (isSafetySave) {
            lastSafetySave.setData(transactions.get(transactions.size() - 1).getOpScore().toString());
        } else {
            lastSafetySave.setData(null);
        }
        lastUpdateService.update(lastSafetySave);
    }

    public void releaseTask() {
        LastUpdate lastUpdate = lastUpdateService.findByIdentifier("fetchTransactions");
        if (lastUpdate != null) {
            lastUpdate.setData(null);
            lastUpdateService.update(lastUpdate);
        }
    }
}
