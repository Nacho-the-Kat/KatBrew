package com.katbrew.workflows.tasks.balance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollection;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.helper.KatBrewWebClient;
import com.katbrew.helper.NftHelper;
import com.katbrew.pojos.NFTCollectionInfoInternal;
import com.katbrew.services.tables.NFTCollectionInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenerateBalances implements JavaDelegate {
    private final GenerateBalancesService generateBalancesService;

    @Override
    public void execute(DelegateExecution execution) throws JsonProcessingException {
        generateBalancesService.generateBalances();
    }
}
