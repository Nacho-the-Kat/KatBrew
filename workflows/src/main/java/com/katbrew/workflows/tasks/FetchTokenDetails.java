package com.katbrew.workflows.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.Holder;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.services.tables.HolderService;
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
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchTokenDetails implements JavaDelegate {
    ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    @Value("${data.fetchTokenBaseUrl}")
    private String tokenUrl;

    private final TokenService tokenService;
    private final HolderService holderService;

    @Override
    public void execute(DelegateExecution execution) {
//        todo
        WebClient client = WebClient.builder().build();
        List<Token> tokenList = tokenService.findBy(Collections.singletonList(Tables.TOKEN.ID.ge(1748)));
        log.info("Starting the token detail sync");

        tokenList.forEach(token -> {
            ParsingResponse<List<ExtendedTokenData>> responseTokenList = client
                    .get()
                    .uri(tokenUrl + "/krc20/token/" + token.getTick())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ParsingResponse<List<ExtendedTokenData>>>() {
                    })
                    .block();
            if (responseTokenList == null) {
                log.error("no data was loaded");
                return;
            }
            Token internal = mapper.convertValue(responseTokenList.getResult().get(0), Token.class);
            internal.setId(token.getId());
            internal.setLogo(token.getLogo());
            tokenService.update(internal);
            //todo
            List<Holder> holder = holderService.findBy(Collections.singletonList(Tables.HOLDER.FK_TOKEN.eq(token.getId())));
        });
    }

    @Data
    private static class ExtendedTokenData extends Token {
        HolderInternal[] holder;
    }

    @Data
    private static class HolderInternal {
        String address;
        BigInteger amount;
    }
}
