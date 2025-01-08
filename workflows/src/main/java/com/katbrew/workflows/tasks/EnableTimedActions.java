package com.katbrew.workflows.tasks;

import com.katbrew.entities.jooq.db.Tables;
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
public class EnableTimedActions implements JavaDelegate {

    private final LastUpdateService lastUpdateService;

    @Override
    public void execute(DelegateExecution execution) {
        final List<LastUpdate> lastUpdateList = lastUpdateService.findBy(List.of(Tables.LAST_UPDATE.IDENTIFIER.in(List.of("tokenFetch", "tokenPriceData", "tokenBalances"))));
        lastUpdateService.batchUpdate(lastUpdateList.stream().peek(single -> single.setData(null)).toList());
        log.info("Finished the initial Data generating: " + LocalDateTime.now());
    }
}
