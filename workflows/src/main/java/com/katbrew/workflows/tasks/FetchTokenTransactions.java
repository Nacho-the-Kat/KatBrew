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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

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

        WebClient client = WebClient.builder().build();
        final List<Token> tokenList = List.of(tokenService.findOne(1775));
        log.info("Starting the transaction sync");
        long start = System.nanoTime();

        for (Token token : tokenList) {
            log.info("starting token: " + token.getTick());
            BigInteger cursor = BigInteger.valueOf(0);
            BigInteger mtdAddLastEntry = null;
            LastUpdate lastUpdate = lastUpdateService.findByIdentifier("fetchTokenTransactions" + token.getTick());
            List<Transaction> transactionList = new ArrayList<>();
            while (cursor != null) {
                ParsingResponse<List<TransactionExternal>> responseTokenList = null;
                try {
                    responseTokenList = client
                            .get()
                            .uri(tokenUrl + "/krc20/oplist?tick=" + token.getTick() + (cursor.compareTo(new BigInteger("0")) > 0 ? "&next=" + cursor : ""))
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<ParsingResponse<List<TransactionExternal>>>() {
                            })
                            .block();

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    log.error("Something wrong with fetching data from " + tokenUrl + "/krc20/oplist?tick=" + token.getTick() + "&next=" + cursor);
                }

                if (responseTokenList != null) {

                    responseTokenList.getResult().forEach(single -> {
                        single.setFromAddress(single.getFrom());
                        single.setToAddress(single.getTo());
                    });
                    final List<Transaction> transactions = mapper.convertValue(responseTokenList.getResult(), new TypeReference<>() {
                    });

                    transactionList.addAll(transactions);
                    cursor = responseTokenList.getNext();

                    if (mtdAddLastEntry == null && !transactions.isEmpty()) {
                        mtdAddLastEntry = transactions.get(transactions.size() - 1).getMtsAdd();
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
                    lastUpdateService.insert(lastUpdate);
                }
            } else {
                if (mtdAddLastEntry != null) {
                    lastUpdate.setData(mtdAddLastEntry.toString());
                    lastUpdateService.update(lastUpdate);
                }
            }

            List<Transaction> dbEntries = transactionService.findBy(List.of(Tables.TRANSACTION.FK_TOKEN.eq(token.getId())));

            transactionList.sort(Comparator.comparing(Transaction::getMtsAdd));

            transactionList.forEach(transaction -> {
                final Transaction dbEntry = dbEntries.stream().filter(intern -> intern.getHashRev().equals(transaction.getHashRev())).findFirst().orElse(null);
                transaction.setFkToken(token.getId());
                if (dbEntry != null) {
                    transaction.setId(dbEntry.getId());
                }
            });

            transactionService.batchUpdate(transactionList.stream().filter(single -> single.getId() != null).toList());
            transactionService.batchInsert(transactionList.stream().filter(single -> single.getId() == null).toList());
            log.info("Done with " + token.getTick() + ", needed time:" + (System.nanoTime() - start));
        }

    }


}
