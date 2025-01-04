package com.katbrew.workflows.tasks;

import com.katbrew.entities.jooq.db.tables.pojos.LastUpdate;
import com.katbrew.services.tables.LastUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LockTask implements JavaDelegate {

    private final LastUpdateService lastUpdateService;

    @Override
    public void execute(DelegateExecution execution) {
        String toLock = (String) execution.getVariable("lastUpdateIdentifier");
        if (toLock == null) {
            return;
        }

        final LastUpdate lastUpdate = lastUpdateService.findByIdentifier(toLock);
        if (lastUpdate != null && lastUpdate.getData() == null) {
            lastUpdate.setData("timed");
            lastUpdateService.update(lastUpdate);
        }
    }
}
