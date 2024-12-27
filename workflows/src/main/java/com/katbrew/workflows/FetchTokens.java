package com.katbrew.workflows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.helper.KatBrewObjectMapper;
import lombok.Data;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class FetchTokens implements JavaDelegate {
    ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        WebClient client = WebClient.builder().baseUrl("https://devkatapi.nachowyborski.xyz").build();
        //todo edit uri
        Response<List<Token>> tokenList = client
                .get()
                .uri("/api/token/tokenlist?limit=10")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Response<List<Token>>>() {})
                .block();
        //todo
        System.out.println("Fetching token starts");
    }

    @Data
    private static class Response<T> {
        T result;
        Integer cursor;

    }
}
