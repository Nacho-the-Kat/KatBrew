package com.katbrew.workflows.tasks;

import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.services.tables.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenerateTokenHolder implements JavaDelegate {

    private final TokenService tokenService;
    private final DSLContext dsl;

    @Override
    public void execute(DelegateExecution execution) {
        final List<Token> tokenList = tokenService.findAll();

        Map<String, Integer> holders = dsl.select(Tables.TOKEN.TICK, DSL.count(Tables.BALANCE.ID))
                .from(Tables.TOKEN)
                .join(Tables.BALANCE).on(Tables.TOKEN.ID.eq(Tables.BALANCE.FK_TOKEN))
                .groupBy(Tables.TOKEN.ID, Tables.TOKEN.TICK)
                .fetch()
                .intoMaps()
                .stream().collect(Collectors.toMap(single -> (String) single.get("tick"), single -> (Integer) single.get("count")));
        tokenList.forEach(token -> {
            final Integer amount = holders.get(token.getTick());
            token.setHolderTotal(Objects.requireNonNullElse(amount, 0));
        });
        tokenService.batchUpdate(tokenList);
    }
}
