package com.katbrew.workflows.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.tables.pojos.PriceData;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.helper.KatBrewWebClient;
import com.katbrew.services.tables.PriceDataService;
import com.katbrew.services.tables.TokenService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchPriceData implements JavaDelegate {
    ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    private final PriceDataService priceDataService;
    private final TokenService tokenService;
    private final WebClient client = KatBrewWebClient.createWebClient();

    @Override
    public void execute(DelegateExecution execution) {
        log.info("Starting the price data update:" + LocalDateTime.now());
        final Map<String, Token> tokens = tokenService.findAll().stream().collect(Collectors.toMap(Token::getTick, single -> single));
        final Map<Integer, String> tokenIdTick = tokens.values().stream().collect(Collectors.toMap(Token::getId, Token::getTick));
        final Map<String, PriceData> priceDatas = priceDataService.findAll().stream().collect(Collectors.toMap(single -> tokens.get(tokenIdTick.get(single.getFkToken())).getTick(), single -> single));

        Map<String, Map> response = client
                .get()
                .uri("https://storage.googleapis.com/kspr-api-v1/marketplace/marketplace.json")
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        if (response != null) {
            List<PriceData> toUpdate = new ArrayList<>();
            List<PriceData> toCreate = new ArrayList<>();
            final ExternalPrice kas = mapper.convertValue(response.get("KAS"), ExternalPrice.class);
            response.keySet().forEach(tick -> {
                final Token token = tokens.get(tick);
                if (token != null) {
                    ExternalPrice externalPrice = mapper.convertValue(response.get(tick), ExternalPrice.class);
                    PriceData priceData = priceDatas.get(tick);
                    if (priceData == null) {
                        priceData = new PriceData();
                        priceData.setFkToken(token.getId());
                        priceData.setValueUsd(externalPrice.getFloor_price());
                        priceData.setValueKas(kas.floor_price * externalPrice.getFloor_price());
                        priceData.setChange_24h(externalPrice.getChange_24h());
                        toCreate.add(priceData);
                    } else {
                        priceData.setValueUsd(externalPrice.getFloor_price());
                        priceData.setChange_24h(externalPrice.getChange_24h());
                        priceData.setValueKas(kas.floor_price * externalPrice.getFloor_price());
                        toUpdate.add(priceData);
                    }
                }
            });
            priceDataService.batchInsert(toCreate);
            priceDataService.batchUpdate(toUpdate);
        }
        log.info("Finished the price data update:" + LocalDateTime.now());
    }

    @Data
    public static class ExternalPrice {
        Double floor_price;
        Double change_24h;
    }

}
