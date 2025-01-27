package com.katbrew.workflows.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class PrintLog implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws JsonProcessingException {
        final String message = (String) execution.getVariable("message");
        final String withTime = (String) execution.getVariable("message");

        if (withTime.equals("true")) {
            log.info(message + ": " + LocalDateTime.now());
        } else {
            log.info(message);
        }
    }
}
