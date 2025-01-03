package com.katbrew.workflows.tasks;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.LastUpdate;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.entities.jooq.db.tables.pojos.Transaction;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.helper.KatBrewWebClient;
import com.katbrew.services.tables.LastUpdateService;
import com.katbrew.services.tables.TokenService;
import com.katbrew.services.tables.TransactionService;
import com.katbrew.workflows.helper.ParsingResponsePaged;
import com.katbrew.workflows.helper.TransactionExternal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchTokenTransactions implements JavaDelegate {
    ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    @Value("${data.fetchTokenBaseUrl}")
    private String tokenUrl;
    private final static Integer batchSize = 100;

    private final TokenService tokenService;
    private final LastUpdateService lastUpdateService;
    private final TransactionService transactionService;
    private final WebClient client = KatBrewWebClient.createWebClient();

    @Override
    public void execute(DelegateExecution execution) throws InterruptedException {
        final LastUpdate isRunning = lastUpdateService.findByIdentifier("tokenTransactions");
        if (isRunning != null) {
            return;
        }
        final LastUpdate update = new LastUpdate();
        update.setIdentifier("tokenTransactions");
        final LastUpdate newLastUpdate = lastUpdateService.insert(update);

        final List<Token> tokenList = tokenService.findAll();
        log.info("Starting the transaction sync:" + LocalDateTime.now());

        final ExecutorService executor = Executors.newFixedThreadPool(batchSize);
        ExecutorCompletionService<Void> completionService = new ExecutorCompletionService<>(executor);

        final ConcurrentMap<String, LastUpdate> lastUpdates = lastUpdateService.findBy(
                List.of(Tables.LAST_UPDATE.IDENTIFIER.startsWith("fetchTokenTransactions"))
        ).stream().collect(Collectors.toConcurrentMap(LastUpdate::getIdentifier, single -> single));

        Lists.partition(tokenList, batchSize).forEach(internalList -> {
            final ConcurrentHashMap<String, Transaction> transactionList = new ConcurrentHashMap<>();
            internalList.forEach(token -> {
                completionService.submit(() -> {
                    Integer cursor = 0;
                    BigInteger mtdAddLastEntry = null;
                    LastUpdate lastUpdate = lastUpdates.get("fetchTokenTransactions" + token.getTick());
                    while (cursor != null) {
                        ParsingResponsePaged<List<TransactionExternal>> responseTokenList = null;
                        try {
                            responseTokenList = client
                                    .get()
                                    .uri(tokenUrl + "/token/operations?tick="
                                            + token.getTick() + (cursor != 0 ? "&page=" + cursor : "")
                                            //if we fetched initially, no need for pageSize over 200
                                            + (lastUpdate != null ? "&pageSize=" + 200 : ""))
                                    .retrieve()
                                    .bodyToMono(new ParameterizedTypeReference<ParsingResponsePaged<List<TransactionExternal>>>() {
                                    })
                                    .block();

                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            log.error("Something wrong with fetching data from " + tokenUrl + "/token/operations?tick=" + token.getTick() + "&next=" + cursor);
                            cursor = null;
                        }

                        if (responseTokenList != null) {

                            responseTokenList.getResult().forEach(single -> {
                                single.setFromAddress(single.getFrom());
                                single.setToAddress(single.getTo());
                                single.setFkToken(token.getId());
                            });
                            final List<Transaction> transactions = mapper.convertValue(responseTokenList.getResult(), new TypeReference<>() {
                            });

                            transactions.forEach(single -> transactionList.put(single.getHashRev(), single));

                            if (!responseTokenList.getPagination().getHasMore() || responseTokenList.getResult().isEmpty()) {
                                cursor = null;
                            } else {
                                cursor += 1;
                            }

                            if (mtdAddLastEntry == null && !transactions.isEmpty()) {
                                mtdAddLastEntry = transactions.get(0).getMtsAdd();
                            }

                            if (lastUpdate != null && mtdAddLastEntry != null) {
                                BigInteger lastMtdAddLastEntry = new BigInteger(lastUpdate.getData());
                                if (mtdAddLastEntry.compareTo(lastMtdAddLastEntry) <= 0) {
                                    //dont need to fetch the next transactions, we only need the newest
                                    cursor = null;
                                }
                            }
                        } else {
                            log.error("no data was loaded");
                        }
                    }

                    if (lastUpdate == null) {
                        lastUpdate = new LastUpdate();
                        if (mtdAddLastEntry != null) {
                            lastUpdate.setData(mtdAddLastEntry.toString());
                            lastUpdate.setIdentifier("fetchTokenTransactions" + token.getTick());
                            lastUpdates.put(lastUpdate.getIdentifier(), lastUpdate);
                        }
                    } else {
                        if (mtdAddLastEntry != null) {
                            lastUpdate.setData(mtdAddLastEntry.toString());
                            lastUpdates.put(lastUpdate.getIdentifier(), lastUpdate);
                        }
                    }
                    return null;
                });
            });
            try {
                //make sure, all tasks are finished
                for (int i = 0; i < internalList.size(); i++) {
                    Future<Void> future = completionService.take();
                    future.get();
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            final Map<String, Transaction> dbEntries = transactionService.findBy(
                    List.of(Tables.TRANSACTION.FK_TOKEN.in(internalList.stream().map(Token::getId).toList()))
            ).stream().collect(Collectors.toMap(Transaction::getHashRev, single -> single));

            final List<Transaction> transactions = new ArrayList<>(transactionList.values());
            transactions.sort(Comparator.comparing(Transaction::getMtsAdd));

            final List<Transaction> toUpdate = new ArrayList<>();
            final List<Transaction> toInsert = new ArrayList<>();

            transactions.forEach(transaction -> {
                final Transaction dbEntry = dbEntries.get(transaction.getHashRev());
                if (dbEntry != null) {
                    transaction.setId(dbEntry.getId());
                    toUpdate.add(transaction);
                } else {
                    toInsert.add(transaction);
                }
            });
            List<LastUpdate> lastUpdateList = new ArrayList<>(lastUpdates.values());
            lastUpdateService.batchUpdate(lastUpdateList.stream().filter(single -> single.getId() != null).toList());
            List<LastUpdate> inserted = lastUpdateService.batchInsert(lastUpdateList.stream().filter(single -> single.getId() == null).toList());
            inserted.forEach(single -> lastUpdates.put(single.getIdentifier(), single));
            transactionService.batchUpdate(toUpdate);
            transactionService.batchInsertVoid(toInsert);
            log.info("Finished a batch:" + LocalDateTime.now());
        });

        executor.shutdown();
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
        lastUpdateService.delete(newLastUpdate);
        log.info("Finished the transaction sync:" + LocalDateTime.now());
    }
}
