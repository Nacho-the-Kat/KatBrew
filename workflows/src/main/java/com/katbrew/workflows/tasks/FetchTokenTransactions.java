package com.katbrew.workflows.tasks;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.LastUpdate;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.entities.jooq.db.tables.pojos.Transaction;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.services.tables.LastUpdateService;
import com.katbrew.services.tables.TokenService;
import com.katbrew.services.tables.TransactionService;
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
import java.util.*;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchTokenTransactions implements JavaDelegate {
    ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    @Value("${data.fetchTokenBaseUrl}")
    private String tokenUrl;

    private final TokenService tokenService;
    private final LastUpdateService lastUpdateService;
    private final TransactionService transactionService;

    @Override
    public void execute(DelegateExecution execution) {
//        todo
        WebClient client = WebClient.builder().build();
        List<Token> tokenList = tokenService.findBy(Collections.singletonList(Tables.TOKEN.ID.ge(1769)));
        log.info("Starting the transaction sync");
        long start = System.nanoTime();
        for (Token token : tokenList) {
            BigInteger cursor = BigInteger.valueOf(0);
            BigInteger firstCursor = null;
            LastUpdate lastUpdate = lastUpdateService.findByIdentifier("fetchTokenTransactions" + token.getTick());
            List<Transaction> transactionList = new ArrayList<>();
            while (cursor != null) {
                ParsingResponse<List<TransactionsInternal>> responseTokenList = null;
                try {
                    responseTokenList = client
                            .get()
                            .uri(tokenUrl + "/krc20/oplist?tick=" + token.getTick() + (cursor.compareTo(new BigInteger("0")) > 0 ? "&next=" + cursor : ""))
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<ParsingResponse<List<TransactionsInternal>>>() {
                            })
                            .block();

                } catch (Exception e) {
                    System.out.println(e);
                    log.error("Something wrong with fetching data from " + tokenUrl + "/krc20/oplist?tick=" + token.getTick() + "&next=" + cursor);
                }

                if (responseTokenList == null) {
                    log.error("no data was loaded");
                    return;
                }
                responseTokenList.getResult().forEach(single -> {
                    single.setFromAddress(single.getFrom());
                    single.setToAddress(single.getTo());
                });
                final List<Transaction> transactions = mapper.convertValue(responseTokenList.getResult(), new TypeReference<>() {});

                transactionList.addAll(transactions);
                cursor = responseTokenList.getNext();

                if (firstCursor == null) {
                    firstCursor = cursor;
                }

                if (lastUpdate != null && cursor != null) {
                    BigInteger lastCursor = new BigInteger(lastUpdate.getData());
                    if (cursor.compareTo(lastCursor) <= 0) {
                        //dont need to fetch the next transactions, we only need the newest
                        cursor = null;
                    }
                }
            }

            if (lastUpdate == null) {
                lastUpdate = new LastUpdate();
                if (firstCursor != null) {
                    lastUpdate.setData(firstCursor.toString());
                    lastUpdate.setIdentifier("fetchTokenTransactions" + token.getTick());
                    lastUpdateService.insert(lastUpdate);
                }
            } else {
                lastUpdate.setData(firstCursor.toString());
                lastUpdateService.update(lastUpdate);
            }

            List<Transaction> dbEntries = transactionService.findBy(List.of(Tables.TRANSACTION.FK_TOKEN.eq(token.getId())));

            transactionList.sort(Comparator.comparing(Transaction::getMtsAdd));
            transactionList = transactionList.stream().collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(Transaction::getHashRev))), ArrayList::new));

            transactionList.forEach(transaction -> {
                final Transaction dbEntry = dbEntries.stream().filter(intern -> intern.getHashRev().equals(transaction.getHashRev())).findFirst().orElse(null);
                transaction.setFkToken(token.getId());
                if (dbEntry != null) {
                    transaction.setId(dbEntry.getId());
                }
            });

            transactionService.update(transactionList.stream().filter(single -> single.getId() != null).toList());
            transactionService.insert(transactionList.stream().filter(single -> single.getId() == null).toList());
            log.info("Done, needed time:" + (System.nanoTime() - start));
        }

    }

    @Data
    private static class TransactionsInternal extends Transaction {
        String from;
        String to;
    }
}
