package com.katbrew.workflows.tasks;

import com.google.common.collect.Lists;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.Balance;
import com.katbrew.entities.jooq.db.tables.pojos.Holder;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.helper.KatBrewWebClient;
import com.katbrew.services.tables.BalanceService;
import com.katbrew.services.tables.HolderService;
import com.katbrew.services.tables.LastUpdateService;
import com.katbrew.services.tables.TokenService;
import com.katbrew.workflows.helper.ParsingResponse;
import lombok.Data;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchTokenBalances implements JavaDelegate {

    @Value("${data.fetchTokenBaseUrl}")
    private String tokenUrl;

    private final TokenService tokenService;
    private final LastUpdateService lastUpdateService;
    private final HolderService holderService;
    private final BalanceService balanceService;
    private final WebClient client = KatBrewWebClient.createWebClient();
    private final static Integer batchSize = 100;

    @Override
    public void execute(DelegateExecution execution) {
        final List<Token> tokenList = tokenService.findAll();
        log.info("Starting the token detail sync:" + LocalDateTime.now());

        final Map<String, Holder> dbHolder = holderService.findAll().stream().collect(Collectors.toMap(Holder::getAddress, single -> single));
        final Map<String, BigInteger> addressId = dbHolder.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, single -> single.getValue().getId()));
        final Map<String, Balance> dbBalances = balanceService.findAll().stream().collect(Collectors.toMap(single -> single.getHolderId() + "-" + single.getFkToken(), single -> single));

        final ExecutorService executor = Executors.newFixedThreadPool(batchSize);
        ExecutorCompletionService<Void> completionService = new ExecutorCompletionService<>(executor);

        try {
            Lists.partition(tokenList, batchSize).forEach(internalList -> {
                final ConcurrentHashMap<String, Holder> holderToCreate = new ConcurrentHashMap<>();
                final ConcurrentHashMap<String, List<Balance>> addressBalances = new ConcurrentHashMap<>();
                internalList.forEach(token -> {
                    completionService.submit(() -> {
                        final ParsingResponse<List<HolderInternal>> responseTokenList = client
                                .get()
                                .uri(tokenUrl + "/token/balances?tick=" + token.getTick())
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<ParsingResponse<List<HolderInternal>>>() {
                                })
                                .block();
                        if (responseTokenList == null) {
                            log.error("no data was loaded");
                            return null;
                        }

                        final List<HolderInternal> holderInternals = responseTokenList.getResult();

                        if (holderInternals != null) {
                            holderInternals.forEach(singleHolder -> {
                                final String address = singleHolder.getAddress();
                                Holder intern = dbHolder.get(address);
                                //completely new holder
                                if (intern == null) {
                                    if (!holderToCreate.containsKey(address)) {
                                        Holder newHolder = new Holder();
                                        newHolder.setAddress(address);
                                        holderToCreate.put(address, newHolder);
                                    }
                                    addressBalances.put(address, List.of(createNewBalance(singleHolder.getBalance(), token)));
                                } else {
                                    final Balance balance = dbBalances.get(addressId.get(address) + "-" + token.getId());
                                    if (!addressBalances.containsKey(address)) {
                                        addressBalances.put(address, new ArrayList<>());
                                    }
                                    if (balance != null) {
                                        //we have a balance for the holder, update it
                                        balance.setBalance(singleHolder.getBalance());
                                        addressBalances.get(address).add(balance);
                                    } else {
                                        //new balance for an old holder
                                        final Balance b = createNewBalance(singleHolder.getBalance(), token);
                                        b.setHolderId(intern.getId());
                                        addressBalances.get(address).add(b);
                                    }
                                }
                            });
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

                holderService.batchInsert(holderToCreate.values().stream().toList()).forEach(single -> {
                    dbHolder.put(single.getAddress(), single);
                    addressId.put(single.getAddress(), single.getId());
                });

                addressBalances.forEach((key, value) -> value.forEach(singleB -> singleB.setHolderId(addressId.get(key))));

                final List<Balance> toUpdate = addressBalances.values().stream().flatMap(single -> single.stream().filter(singleB -> singleB.getId() != null)).toList();
                final List<Balance> newBalances = balanceService.batchInsert(addressBalances.values().stream().flatMap(single -> single.stream().filter(singleB -> singleB.getId() == null)).toList());
                balanceService.batchUpdate(toUpdate);
                final List<Balance> combined = Stream.concat(toUpdate.stream(), newBalances.stream()).toList();
                final List<Balance> toDelete = balanceService.findBy(List.of(
                        Tables.BALANCE.ID.notIn(combined.stream().map(Balance::getId).toList()),
                        Tables.BALANCE.FK_TOKEN.in(internalList.stream().map(Token::getId).toList())
                ));
                balanceService.batchDelete(toDelete);
                toDelete.forEach(balance -> dbBalances.remove(balance.getHolderId() + "-" + balance.getFkToken()));
                log.info("Finished a balances batch:" + LocalDateTime.now());
            });
            executor.shutdown();
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.info("Threads not threading anymore");
        }
        log.info("Updateing balances done: " + LocalDateTime.now());
    }

    private Balance createNewBalance(final BigInteger amount, final Token token) {
        final Balance balance = new Balance();
        balance.setBalance(amount);
        balance.setFkToken(token.getId());
        return balance;
    }

    @Data
    private static class HolderInternal {
        String address;
        BigInteger balance;
        BigInteger locked;
    }
}
