package com.katbrew.workflows.tasks.transactions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.services.tables.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenerateTokenBatches implements JavaDelegate {
    private final static Integer batchSize = 100;
    private final ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    private final TokenService tokenService;

    @Override
    public void execute(DelegateExecution execution) throws JsonProcessingException {

        final List<Token> tokenList = tokenService.findAll();
        final List<String> batches = Lists.partition(tokenList, batchSize).stream().map(single -> {
            try {
                return mapper.writeValueAsString(single);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        execution.setVariable("tokenBatches", batches);
    }
}
