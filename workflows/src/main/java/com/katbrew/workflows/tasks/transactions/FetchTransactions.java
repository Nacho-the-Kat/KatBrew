package com.katbrew.workflows.tasks.transactions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.*;
import com.katbrew.helper.KatBrewHelper;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.pojos.TransactionExternal;
import com.katbrew.services.tables.*;
import com.katbrew.workflows.helper.ParsingResponse;
import com.katbrew.workflows.helper.ParsingResponsePagedNFT;
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
import java.util.concurrent.TimeUnit;
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
    private final FetchDataService fetchDataService;
    private final TransactionService transactionService;
    private final TokenService tokenService;
    private final HolderService holderService;
    public final DSLContext dsl;
    public final com.katbrew.entities.jooq.db.tables.Transaction transactionTable = Tables.TRANSACTION;

    private static String getCursor(ParsingResponsePagedNFT<List<TransactionExternal>> entity) {
        return entity.getNext();
    }

    @Override
    public void execute(DelegateExecution execution) throws JsonProcessingException {
        log.info("Starting the transaction sync:" + LocalDateTime.now());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            final List<Token> tokens = tokenService.findAll();
            tokens.sort(Comparator.comparing(Token::getMtsAdd));

            final ConcurrentMap<String, BigInteger> holderMap = holderService.getAddressIdMap();

            final ConcurrentMap<String, FetchData> lastUpdates = fetchDataService.findBy(
                    List.of(
                            Tables.FETCH_DATA.IDENTIFIER.in(tokens.stream().map(single -> "fetchTokenTransactionsLastCursor" + single.getTick()).toList())
                    )
            ).stream().collect(Collectors.toConcurrentMap(FetchData::getIdentifier, single -> single));

            final ConcurrentMap<String, FetchData> lastUpdatesSafety = fetchDataService.findBy(
                    List.of(
                            Tables.FETCH_DATA.IDENTIFIER.in(tokens.stream().map(single -> "fetchTransactionsSafetySave" + single.getTick()).toList())
                    )
            ).stream().collect(Collectors.toConcurrentMap(FetchData::getIdentifier, single -> single));

            final ParameterizedTypeReference<ParsingResponsePagedNFT<List<TransactionExternal>>> reference = new ParameterizedTypeReference<>() {
            };

            final KatBrewHelper<ParsingResponsePagedNFT<List<TransactionExternal>>, TransactionExternal> helper = new KatBrewHelper<>();


            final ExecutorService executor = Executors.newFixedThreadPool(10);

            try {
                for (final Token token : tokens) {
                    executor.submit(() -> {
                        try {
                            String uri = fetchBaseUrl + "/oplist?tick=" + token.getTick();
                            FetchData safety = lastUpdatesSafety.get("fetchTransactionsSafetySave" + token.getTick());
                            FetchData lastCursor = safety != null && safety.getData() != null
                                    ? safety
                                    : lastUpdates.get("fetchTokenTransactionsLastCursor" + token.getTick());
                            log.info("Start fetching the Transactions, last cursor: " + (lastCursor != null ? lastCursor.getData() : "not exists"));

                            final List<TransactionExternal> result = helper.fetchPaginated(
                                    uri,
                                    lastCursor != null ? lastCursor.getData() : null,
                                    true,
                                    "&next=",
                                    reference,
                                    FetchTransactions::getCursor,
                                    ParsingResponse::getResult,
                                    limit,
                                    internResult -> prepareData(internResult, token, holderMap, true)
                            );

                            if (result != null) {
                                prepareData(result, token, holderMap, false);
                            } else {
                                log.error("no transactions were loaded for tick " + token.getTick());
                            }

                            log.info("Finished the transaction fetching for tick: " + token.getTick() + " at " + LocalDateTime.now());
                        } catch (Exception e) {
                            log.error(e.getMessage());
                        }
                    });
                }
            } catch (Exception e) {
                log.info(e.getMessage());
            }
            executor.shutdown();
            try {
                executor.awaitTermination(8, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            generateLastUpdateTransactionsService.execute();
            releaseTask();
            log.info("Finished the transaction sync:" + LocalDateTime.now());
            executorService.shutdown();
        });
    }

    public Void prepareData(
            final List<TransactionExternal> result,
            final Token token,
            final ConcurrentMap<String, BigInteger> holderMap,
            final Boolean isSafetySave
    ) {
        result.forEach(single -> {
            if (single.getFrom() != null) {
                BigInteger from = holderMap.get(single.getFrom());
                if (from == null) {
                    final Holder newHolder = new Holder();
                    newHolder.setAddress(single.getFrom());
                    try {
                        final Holder created = holderService.insert(newHolder);
                        holderMap.put(created.getAddress(), created.getId());
                        from = created.getId();
                    } catch (Exception e) {
                        log.info("catching address already inserted: id " + holderMap.get(single.getFrom()));
                        from = holderMap.get(single.getFrom());
                    }
                }
                single.setFromAddress(from);
            }
            if (single.getTo() != null) {
                BigInteger to = holderMap.get(single.getTo());
                if (to == null) {
                    try {
                        final Holder newHolder = new Holder();
                        newHolder.setAddress(single.getTo());
                        final Holder created = holderService.insert(newHolder);
                        holderMap.put(created.getAddress(), created.getId());
                        to = created.getId();
                    } catch (Exception e) {
                        log.info("catching address already inserted: id " + holderMap.get(single.getTo()));
                        to = holderMap.get(single.getTo());
                    }
                }
                single.setToAddress(to);
            }
            single.setFkToken(token.getId());
        });
        final List<Transaction> transactions = mapper.convertValue(result, new TypeReference<>() {
        });
        updateAndInsertAll(transactions, token, isSafetySave);
        return null;
    }


    private void updateAndInsertAll(final List<Transaction> transactions, final Token token, final Boolean isSafetySave) {

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

        //we update/add the transaction count, no full fetch needed;
        final int amount = toInsert.size();
        token.setTransferTotal(
                token.getTransferTotal().add(BigInteger.valueOf(amount))
        );

        tokenService.update(token);

        FetchData lastSafetySave = fetchDataService.findByIdentifier("fetchTransactionsSafetySave" + token.getTick());
        if (lastSafetySave == null) {
            lastSafetySave = new FetchData();
            lastSafetySave.setIdentifier("fetchTransactionsSafetySave" + token.getTick());
            lastSafetySave = fetchDataService.insert(lastSafetySave);
        }
        if (isSafetySave) {
            lastSafetySave.setData(transactions.get(transactions.size() - 1).getOpScore().toString());
        } else {
            lastSafetySave.setData(null);
        }
        fetchDataService.update(lastSafetySave);
    }

    public void releaseTask() {
        LastUpdate lastUpdate = lastUpdateService.findByIdentifier("fetchTransactions");
        if (lastUpdate != null) {
            lastUpdate.setData(null);
            lastUpdateService.update(lastUpdate);
        }
    }
}
