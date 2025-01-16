package com.katbrew.workflows.tasks;

import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.helper.KatBrewHelper;
import com.katbrew.services.tables.TokenService;
import com.katbrew.workflows.helper.ParsingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchTokens implements JavaDelegate {

    @Value("${data.fetchTokenBaseUrl}")
    private String tokenUrl;

    private final TokenService tokenService;
    private final KatBrewHelper<ParsingResponse<List<Token>>, Token> client = new KatBrewHelper<>(null);

    @Override
    public void execute(DelegateExecution execution) throws InterruptedException {

        log.info("Starting the token sync: " + LocalDateTime.now());

        final ParsingResponse<List<Token>> responseTokenList = client
                .fetch(
                        tokenUrl + "/tokens",
                        new ParameterizedTypeReference<>() {
                        }
                );
        if (responseTokenList == null) {
            log.error("no token data was loaded");
            return;
        }

        final List<Token> tokenList = responseTokenList.getResult();

        final Map<String, Token> dbEntries = tokenService.findAll().stream().collect(Collectors.toMap(Token::getTick, single -> single));

        tokenList.forEach(single -> {
            final Token token = dbEntries.get(single.getTick());
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

        tokenService.batchInsertVoid(tokenList.stream().filter(single -> single.getId() == null).toList());
        tokenService.batchUpdate(tokenList.stream().filter(single -> single.getId() != null).toList());

        log.info("Finished the token sync: " + LocalDateTime.now());
    }

}
