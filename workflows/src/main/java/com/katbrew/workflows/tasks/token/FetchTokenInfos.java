package com.katbrew.workflows.tasks.token;

import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.helper.KatBrewHelper;
import com.katbrew.services.helper.TokenCachingService;
import com.katbrew.services.tables.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchTokenInfos implements JavaDelegate {

    private final TokenService tokenService;
    private final TokenCachingService tokenCachingService;
    private final KatBrewHelper<Map<String, Object>, HashMap<String, Object>> client = new KatBrewHelper<>();

    @Override
    public void execute(DelegateExecution execution) throws InterruptedException {

        log.info("Starting the token detail sync: " + LocalDateTime.now());

        final List<Token> token = tokenService.findAll();
        final ParameterizedTypeReference<Map<String, Object>> reference = new ParameterizedTypeReference<>() {
        };
        final ExecutorService executor = Executors.newFixedThreadPool(100);
        token.forEach(single -> executor.submit(() -> {
            try {
                final Map<String, Object> info = client.fetch("https://api-v2-do.kas.fyi/token/krc20/" + single.getTick() + "/info", reference);
                single.setTransferTotal(new BigInteger(info.get("transferTotal").toString()));
                single.setMintTotal(Integer.parseInt(info.get("mintTotal").toString()));
                single.setHolderTotal(Integer.parseInt(info.get("holderTotal").toString()));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);
        tokenService.batchUpdate(token);
        tokenCachingService.invalidateTokenList();
        log.info("Finished the token detail sync: " + LocalDateTime.now());
    }
}