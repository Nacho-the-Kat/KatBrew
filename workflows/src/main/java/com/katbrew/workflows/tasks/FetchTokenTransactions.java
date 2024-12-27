package com.katbrew.workflows.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.LastUpdate;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.entities.jooq.db.tables.pojos.Transaction;
import com.katbrew.helper.KatBrewObjectMapper;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchTokenTransactions implements JavaDelegate {
    ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    @Value("${data.fetchTokenBaseUrl}")
    private String tokenUrl;

    private final TokenService tokenService;
    private final LastUpdateService lastUpdateService;

    @Override
    public void execute(DelegateExecution execution) {
//        todo
        WebClient client = WebClient.builder().build();
        List<Token> tokenList = tokenService.findBy(Collections.singletonList(Tables.TOKEN.ID.ge(1748)));
        log.info("Starting the transaction sync");

        tokenList.forEach(token -> {
            BigInteger cursor = BigInteger.valueOf(0);
            BigInteger firstCursor = null;
            LastUpdate lastUpdate = lastUpdateService.findByIdentifier("fetchTokenTransactions" + token.getTick());
            List<Transaction> transactionList = new ArrayList<>();
            while (cursor != null) {

                ParsingResponse<List<Transaction>> responseTokenList = client
                        .get()
                        .uri(tokenUrl + "/krc20/oplist?tick=" + token.getTick() + (cursor.compareTo(new BigInteger("0")) > 0 ? "?next=" + cursor : ""))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<ParsingResponse<List<Transaction>>>() {
                        })
                        .block();

                if (responseTokenList == null) {
                    log.error("no data was loaded");
                    return;
                }

                transactionList.addAll(responseTokenList.getResult());
                cursor = responseTokenList.getNext();

                if (firstCursor == null) {
                    firstCursor = cursor;
                }

                if (lastUpdate != null) {
                    BigInteger lastCursor = new BigInteger(lastUpdate.getData());
                    if (cursor.compareTo(lastCursor) <= 0) {
                        //dont need to fetch the next tokens, we only need the newest
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

//            todo update and insert
        });

    }
//
//    @Data
//    private static class ExtendedTokenData extends Token {
//        HolderInternal[] holder;
//    }
//
//    @Data
//    private static class HolderInternal {
//        String address;
//        BigInteger amount;
//    }
}
