package com.katbrew.workflows.tasks.transactions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.LastUpdate;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.entities.jooq.db.tables.pojos.Transaction;
import com.katbrew.services.tables.LastUpdateService;
import com.katbrew.services.tables.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenerateLastUpdateTransactions implements JavaDelegate {

    private final LastUpdateService lastUpdateService;
    private final TokenService tokenService;
    private final DSLContext dslContext;

    @Override
    public void execute(DelegateExecution execution) throws JsonProcessingException {
        final List<Token> tokens = tokenService.findAll();
        final Map<String, LastUpdate> lastUpdates = lastUpdateService.findBy(List.of(
                Tables.LAST_UPDATE.IDENTIFIER.in(tokens.stream().map(single -> "fetchTokenTransactions" + single.getTick()).toList())
        )).stream().collect(Collectors.toMap(LastUpdate::getIdentifier, single -> single));

        final List<LastUpdate> toUpdate = new ArrayList<>();
        final List<LastUpdate> toInsert = new ArrayList<>();

        tokens.forEach(token -> {
            final String identifier = "fetchTokenTransactions" + token.getTick();
            final List<Transaction> lastTransactionList = dslContext.selectFrom(Tables.TRANSACTION)
                    .where(Tables.TRANSACTION.FK_TOKEN.eq(token.getId()))
                    .orderBy(Tables.TRANSACTION.MTS_ADD.desc())
                    .limit(1)
                    .fetch()
                    .into(Transaction.class);
            if (!lastTransactionList.isEmpty()) {
                final Transaction lastTransaction = lastTransactionList.get(0);
                final LastUpdate tokenUpdate = lastUpdates.get(identifier);
                if (tokenUpdate == null) {
                    final LastUpdate insert = new LastUpdate();
                    insert.setData(lastTransaction.getMtsAdd().toString());
                    insert.setIdentifier(identifier);
                    toInsert.add(insert);
                } else {
                    tokenUpdate.setData(lastTransaction.getMtsAdd().toString());
                    toUpdate.add(tokenUpdate);
                }
            } else {
                log.info("No transactions for " + token.getTick());
            }
        });
        lastUpdateService.batchInsertVoid(toInsert);
        lastUpdateService.batchUpdate(toUpdate);
    }
}
