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
import com.katbrew.services.tables.HolderService;
import com.katbrew.services.tables.LastUpdateService;
import com.katbrew.services.tables.TokenService;
import com.katbrew.services.tables.TransactionService;
import com.katbrew.workflows.helper.ParsingResponse;
import com.katbrew.workflows.helper.ParsingResponsePaged;
import com.katbrew.pojos.TransactionExternal;
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
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchTokenTransactions implements JavaDelegate {
    private final ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    @Value("${data.fetchTokenBaseUrl}")
    private String tokenUrl;
    private final static Integer processLimit = 20;
    private final static int limit = 50000;
    private final GenerateLastUpdateTransactionsService generateLastUpdateTransactionsService;
    private final LastUpdateService lastUpdateService;
    private final TransactionService transactionService;
    private final TokenService tokenService;
    private final HolderService holderService;
    public final DSLContext dsl;
    public final com.katbrew.entities.jooq.db.tables.Transaction transactionTable = Tables.TRANSACTION;

    private static String getCursor(ParsingResponsePaged<List<TransactionExternal>> entity) {
        if (entity.getPagination().getHasMore()) {
            return entity.getResult().get(entity.getResult().size() - 1).getOpScore().toString();
        }
        return null;
    }

    @Override
    public void execute(DelegateExecution execution) throws JsonProcessingException {
        log.info("Starting the transaction sync:" + LocalDateTime.now());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            final List<Token> tokens = tokenService.findAll();

            final ExecutorService executor = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
            final ExecutorCompletionService<Void> completionService = new ExecutorCompletionService<>(executor);

            final ConcurrentMap<String, LastUpdate> lastUpdates = lastUpdateService.findBy(
                    List.of(
                            Tables.LAST_UPDATE.IDENTIFIER.in(tokens.stream().map(single -> "fetchTokenTransactions" + single.getTick()).toList())
                    )
            ).stream().collect(Collectors.toConcurrentMap(LastUpdate::getIdentifier, single -> single));

            final ConcurrentMap<String, BigInteger> holderMap = holderService.getAddressIdMap();
            final ParameterizedTypeReference<ParsingResponsePaged<List<TransactionExternal>>> reference = new ParameterizedTypeReference<>() {
            };

            final KatBrewHelper<ParsingResponsePaged<List<TransactionExternal>>, TransactionExternal> helper = new KatBrewHelper<>();

            try {
//                log.info("Starting a transaction sync batch:" + LocalDateTime.now());
                final AtomicInteger runningTasks = new AtomicInteger();
                for (Token token : tokens) {
                    runningTasks.getAndIncrement();
                    completionService.submit(() -> {
                        log.info("Starting Token:" + token.getTick());
                        int time = (int) (Math.random() * 10);
                        long start = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                        while (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - start <= time) {
                            //waiting that not every thread starts at the same time
                        }
                        LastUpdate lastUpdate = lastUpdates.get("fetchTokenTransactions" + token.getTick());
                        String uri = tokenUrl + "/token/operations?tick=" + token.getTick();

                        //todo uncomment after sync
                        if (lastUpdate != null) {
                            //if we fetched initially, no need for pageSize over 200
                            uri += "&pageSize=" + 200;
                        }else{
                            log.info("no transactions for " + token.getTick() + " starting full fetch");
                        }
                        final List<TransactionExternal> result = helper.fetchPaginated(
                                uri,
                                lastUpdate != null ? lastUpdate.getData() : null,
                                true,
                                "&lastScore=",
                                reference,
                                FetchTokenTransactions::getCursor,
                                ParsingResponse::getResult,
                                limit,
                                internResult -> prepareData(internResult, token, holderMap)
                        );

                        if (result != null) {
                            prepareData(result, token, holderMap);
                            generateLastUpdateTransactionsService.execute(token);
                        } else {
                            log.error("no data was loaded for " + token.getTick());
                        }
                        runningTasks.getAndDecrement();
                        return null;
                    });

                    while (runningTasks.get() == processLimit) {

                    }

                }
                while (runningTasks.get() != 0) {

                }
                log.info("Finished a transaction batch:" + LocalDateTime.now());

            } catch (Exception e) {
                log.info(e.getMessage());
            }
            try {
                executor.shutdown();
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                log.info(e.getMessage());
            }
            releaseTask();
            log.info("Finished the transaction sync:" + LocalDateTime.now());
            executorService.shutdown();
        });
    }

    public Void prepareData(
            final List<TransactionExternal> result,
            final Token token,
            final ConcurrentMap<String, BigInteger> holderMap
    ) {

        result.forEach(single -> {
            BigInteger from = holderMap.get(single.getFrom());
            BigInteger to = holderMap.get(single.getTo());
            if (from == null){
                final Holder newHolder = new Holder();
                newHolder.setAddress(single.getFrom());
                final Holder created = holderService.insert(newHolder);
                from = created.getId();
                holderMap.put(created.getAddress(), created.getId());
            }
            if (to == null){
                final Holder newHolder = new Holder();
                newHolder.setAddress(single.getTo());
                final Holder created = holderService.insert(newHolder);
                to = created.getId();
                holderMap.put(created.getAddress(), created.getId());
            }
            single.setFromAddress(from);
            single.setToAddress(to);
            single.setFkToken(token.getId());
        });
        final List<Transaction> transactions = mapper.convertValue(result, new TypeReference<>() {
        });
        //to remove duplicates
        final HashMap<String, Transaction> intern = new HashMap<>();
        transactions.forEach(single -> intern.put(single.getHashRev(), single));
        //we insert such big data separately
        updateAndInsertSingle(token, intern);
        return null;
    }

    private void updateAndInsertSingle(final Token token, final Map<String, Transaction> transactionMap) {
        updateAndInsertAll(List.of(token), transactionMap);
    }

    private void updateAndInsertAll(final List<Token> batch, final Map<String, Transaction> transactionMap) {

        final List<Transaction> transactions = new ArrayList<>(transactionMap.values());
        final Map<String, BigInteger> dbEntries = dsl
                .select(transactionTable.ID, transactionTable.HASH_REV)
                .from(transactionTable)
                .where(List.of(
                                batch.size() == 1
                                        ? transactionTable.FK_TOKEN.eq(batch.get(0).getId())
                                        : transactionTable.FK_TOKEN.in(batch.stream().map(Token::getId).toList()),
                                transactionTable.HASH_REV.in(transactions.stream().map(Transaction::getHashRev).toList())
                        )
                )
                .fetch()
                .intoMaps()
                .stream()
                .collect(Collectors.toMap(single -> (String) single.get("hash_rev"), single -> new BigInteger(String.valueOf(single.get("id")))));

        transactions.sort(Comparator.comparing(Transaction::getMtsAdd));

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
    }

    public void releaseTask() {
        LastUpdate lastUpdate = lastUpdateService.findByIdentifier("tokenTransactions");
        if (lastUpdate == null) {
            lastUpdate = new LastUpdate();
            lastUpdate.setIdentifier("tokenTransactions");
            lastUpdate.setData(null);
            lastUpdateService.insert(lastUpdate);
        } else {
            lastUpdate.setData(null);
            lastUpdateService.update(lastUpdate);
        }
    }
}
