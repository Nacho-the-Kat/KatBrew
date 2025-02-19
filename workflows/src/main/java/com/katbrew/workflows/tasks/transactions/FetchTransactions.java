package com.katbrew.workflows.tasks.transactions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.*;
import com.katbrew.helper.EntityConverter;
import com.katbrew.helper.KatBrewHelper;
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

    @Value("${data.fetchTokenBaseUrl}")
    private String fetchBaseUrl;
    private final static int limit = 15000;
    private final LastUpdateService lastUpdateService;
    private final CodeWordingsService codeWordingsService;
    private final FetchDataService fetchDataService;
    private final TransactionService transactionService;
    private final TokenService tokenService;
    private final HolderService holderService;
    private final EntityConverter entityConverter;
    public final DSLContext dsl;
    public final List<Transaction> toInsert = new ArrayList<>();
    public final List<Transaction> toUpdate = new ArrayList<>();

    public final com.katbrew.entities.jooq.db.tables.Transaction transactionTable = Tables.TRANSACTION;

    private static String getCursor(ParsingResponsePagedNFT<List<TransactionExternal>> entity) {
        return entity.getNext();
    }

    private Map<String, Integer> codes;

    @Override
    public void execute(DelegateExecution execution) throws JsonProcessingException {
        log.info("Starting the transaction sync:" + LocalDateTime.now());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            final List<Token> tokens = tokenService.findAll();
            tokens.sort(Comparator.comparing(Token::getMtsAdd));
            codes = codeWordingsService.getAsMapWithNull();

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

                            final List<TransactionExternal> result = helper.fetchPaginated(
                                    uri,
                                    lastCursor != null ? lastCursor.getData() : null,
                                    true,
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
            transactionService.batchUpdate(toUpdate);
            toInsert.sort(Comparator.comparing(Transaction::getOpScore));
            final List<Transaction> inserted = transactionService.batchInsertWithSub(toInsert);
            final List<FetchData> fetchDataToUpdate = new ArrayList<>();
            final List<FetchData> fetchDataToInsert = new ArrayList<>();
            for (final Token token : tokens) {
                //we update/add the transaction count, no full fetch needed;
                final List<Transaction> tokenTransaction = inserted.stream().filter(single -> single.getFkToken().equals(token.getId())).sorted(Comparator.comparing(Transaction::getOpScore)).toList();
                if (!tokenTransaction.isEmpty()) {
                    final String identifier = "fetchTokenTransactionsLastCursor" + token.getTick();
                    final FetchData lastUpdate = fetchDataService.findByIdentifier(identifier);
                    if (lastUpdate == null) {
                        final FetchData insert = new FetchData();
                        insert.setData(tokenTransaction.get(tokenTransaction.size() - 1).getOpScore().toString());
                        insert.setIdentifier(identifier);
                        fetchDataToInsert.add(insert);
                    } else {
                        lastUpdate.setData(tokenTransaction.get(tokenTransaction.size() - 1).getOpScore().toString());
                        fetchDataToUpdate.add(lastUpdate);
                    }
                }
            }
            fetchDataService.batchUpdate(fetchDataToUpdate);
            fetchDataService.batchInsert(fetchDataToInsert);
            final LastUpdate lastUpdate = lastUpdateService.findByIdentifier("fetchTransactions");
            if (lastUpdate != null) {
                lastUpdate.setData(null);
                lastUpdateService.update(lastUpdate);
            }
            toInsert.clear();
            toUpdate.clear();
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
        final List<Transaction> transactions = new ArrayList<>(result.stream().map(entityConverter::convertToDbTransaction).toList());
        updateAndInsertAll(transactions, token, isSafetySave);
        return null;
    }


    private void updateAndInsertAll(final List<Transaction> transactions, final Token token, final Boolean isSafetySave) {

        final Map<String, Transaction> dbEntries = dsl
                .selectFrom(transactionTable)
                .where(List.of(
                                transactionTable.HASH_REV.in(transactions.stream().map(Transaction::getHashRev).toList())
                        )
                )
                .fetch()
                .into(Transaction.class)
                .stream()
                .collect(Collectors.toMap(Transaction::getHashRev, single -> single));

        transactions.sort(Comparator.comparing(Transaction::getOpScore));

        transactions.forEach(transaction -> {
            final Transaction dbEntry = dbEntries.get(transaction.getHashRev());
            if (dbEntry != null) {
                transaction.setId(dbEntry.getId());
                if (!transaction.equals(dbEntry)) {
                    toUpdate.add(transaction);
                }
            } else {
                toInsert.add(transaction);
            }
        });

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
}
