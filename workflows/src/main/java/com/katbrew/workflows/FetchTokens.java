package com.katbrew.workflows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.services.base.ApiResponse;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

@Component
public class FetchTokens implements JavaDelegate {
    ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        WebClient client = WebClient.builder().baseUrl("https://devkatapi.nachowyborski.xyz").build();
        String te = client.get().uri("/api/token/tokenlist?limit=10").retrieve().bodyToMono(String.class).block();
//        System.out.println(te.getResult().size());        new Parameterized
//        te.getResult().forEach(Token::getTick);
        JSONObject obj = mapper.convertValue(te, JSONObject.class);

        List<Token> e = mapper.convertValue(obj.get("result") , new TypeReference<List<Token>>(){});
        System.out.println("Fetching token starts");
    }
}
