package com.katbrew.workflows.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.Tables;
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
public class UpdateTokenTransactions implements JavaDelegate {
    ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    @Value("${data.fetchTokenBaseUrl}")
    private String tokenUrl;

    private final TransactionService transactionService;

    @Override
    public void execute(DelegateExecution execution) {
        //todo which are pending?
//        WebClient client = WebClient.builder().build();
//        final List<Transaction> transactionList = transactionService.findBy(List.of(Tables.TRANSACTION.OP_ACCEPT.isNull()));
//        log.info("Starting the transaction update");
//        long start = System.nanoTime();
//        final List<Transaction> transactions = new ArrayList<>();
//        for (Transaction transaction : transactionList) {
//
//            ParsingResponse<TransactionExternal> responseTransfer = client
//                    .get()
//                    .uri(tokenUrl + "/krc20/oplist?tick=" + token.getTick() + (cursor.compareTo(new BigInteger("0")) > 0 ? "&next=" + cursor : ""))
//                    .retrieve()
//                    .bodyToMono(new ParameterizedTypeReference<ParsingResponse<TransactionsInternal>>() {
//                    })
//                    .block();
//        }
//
//        List<Transaction> dbEntries = transactionService.findBy(List.of(Tables.TRANSACTION.FK_TOKEN.eq(token.getId())));
//
//        transactionList.sort(Comparator.comparing(Transaction::getMtsAdd));
//        transactionList = transactionList.stream().collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(Transaction::getHashRev))), ArrayList::new));
//
//        transactionList.forEach(transaction -> {
//            final Transaction dbEntry = dbEntries.stream().filter(intern -> intern.getHashRev().equals(transaction.getHashRev())).findFirst().orElse(null);
//            transaction.setFkToken(token.getId());
//            if (dbEntry != null) {
//                transaction.setId(dbEntry.getId());
//            }
//        });
//
//        transactionService.batchUpdate(transactionList.stream().filter(single -> single.getId() != null).toList());
    }

}
