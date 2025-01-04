package com.katbrew.workflows.tasks;

import com.katbrew.entities.jooq.db.tables.pojos.LastUpdate;
import com.katbrew.services.tables.LastUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenerateInitialData implements JavaDelegate {

    private final LastUpdateService lastUpdateService;

    public static List<String> initData = List.of("tokenTransactions", "tokenFetch", "tokenPriceData", "tokenBalances");

    @Override
    public void execute(DelegateExecution execution) {
        log.info("Starting the initial Data generating: " + LocalDateTime.now());
        lastUpdateService.batchInsert(
                initData.stream().map(single -> {
                    final LastUpdate lastUpdate = new LastUpdate();
                    lastUpdate.setIdentifier(single);
                    lastUpdate.setData("init");
                    return lastUpdate;
                }).toList()
        );
    }
}
