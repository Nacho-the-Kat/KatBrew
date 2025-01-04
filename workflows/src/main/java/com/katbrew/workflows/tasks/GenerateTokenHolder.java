package com.katbrew.workflows.tasks;

import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.services.tables.BalanceService;
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
public class GenerateTokenHolder implements JavaDelegate {

    private final TokenService tokenService;
    private final BalanceService balanceService;

    @Override
    public void execute(DelegateExecution execution) {
        final List<Token> tokenList = tokenService.findAll();

        tokenList.forEach(token -> {
            token.setHolderTotal(balanceService.findBy(List.of(Tables.BALANCE.FK_TOKEN.eq(token.getId()))).size());
        });
        tokenService.batchUpdate(tokenList);
    }
}
