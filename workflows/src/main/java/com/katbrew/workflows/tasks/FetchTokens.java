package com.katbrew.workflows.tasks;

import com.katbrew.entities.jooq.db.tables.pojos.LastUpdate;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.services.tables.LastUpdateService;
import com.katbrew.services.tables.TokenService;
import com.katbrew.workflows.helper.ParsingResponse;
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

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchTokens implements JavaDelegate {

    @Value("${data.fetchTokenBaseUrl}")
    private String tokenUrl;

    private final TokenService tokenService;
    private final LastUpdateService lastUpdateService;

    @Override
    public void execute(DelegateExecution execution) {
        WebClient client = WebClient.builder().build();
        List<Token> tokenList = new ArrayList<>();
        BigInteger cursor = BigInteger.valueOf(0);
        BigInteger firstCursor = null;
        log.info("Starting the token sync");
        LastUpdate lastUpdate = lastUpdateService.findByIdentifier("fetchToken");

        while (cursor != null) {
            ParsingResponse<List<Token>> responseTokenList = client
                    .get()
                    .uri(tokenUrl + "/krc20/tokenlist" + (cursor.compareTo(new BigInteger("0")) > 0 ? "?next=" + cursor : ""))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ParsingResponse<List<Token>>>() {
                    })
                    .block();
            if (responseTokenList == null) {
                log.error("no data was loaded");
                return;
            }

            tokenList.addAll(responseTokenList.getResult());
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
                lastUpdate.setIdentifier("fetchToken");
                lastUpdateService.insert(lastUpdate);
            }
        } else {
            lastUpdate.setData(firstCursor.toString());
            lastUpdateService.update(lastUpdate);
        }

        List<Token> dbEntries = tokenService.findByTicks(tokenList.stream().map(Token::getTick).toList());

        tokenList.forEach(single -> {
            Token token = dbEntries.stream().filter(intern -> intern.getTick().equals(single.getTick())).findFirst().orElse(null);
            if (token != null) {
                single.setId(token.getId());
                single.setHolderTotal(token.getHolderTotal());
                single.setTransferTotal(token.getTransferTotal());
                single.setMintTotal(token.getMintTotal());
                single.setLogo(token.getLogo());
            }
        });
        //Sorting by the creation time to get entries in the db as they created
        tokenList.sort(Comparator.comparing(Token::getMtsAdd));

        final List<Token> updateToken = tokenList.stream().filter(single -> single.getId() != null).toList();

        tokenService.insert(tokenList.stream().filter(single -> single.getId() == null).toList());
        tokenService.update(updateToken);

    }

}
