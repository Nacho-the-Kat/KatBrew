package com.katbrew.workflows.tasks.transactions;

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

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenerateLastUpdateTransactionsAllTokens implements JavaDelegate {

    private final LastUpdateService lastUpdateService;
    private final TokenService tokenService;
    private final DSLContext dslContext;

    @Override
    public void execute(DelegateExecution execution) {
        final List<Token> tokens = tokenService.findAll();
        tokens.forEach(token -> {

            final LastUpdate lastUpdate = lastUpdateService.findByIdentifier("fetchTokenTransactions" + token.getTick());

            final String identifier = "fetchTokenTransactions" + token.getTick();
            final List<Transaction> lastTransactionList = dslContext
                    .select(Tables.TRANSACTION.ID, Tables.TRANSACTION.OP_SCORE)
                    .from(Tables.TRANSACTION)
                    .where(Tables.TRANSACTION.FK_TOKEN.eq(token.getId()))
                    .orderBy(Tables.TRANSACTION.OP_SCORE.desc())
                    .limit(1)
                    .fetch()
                    .into(Transaction.class);
            if (!lastTransactionList.isEmpty()) {
                final Transaction lastTransaction = lastTransactionList.get(0);

                if (lastUpdate == null) {
                    final LastUpdate insert = new LastUpdate();
                    insert.setData(lastTransaction.getOpScore().toString());
                    insert.setIdentifier(identifier);
                    lastUpdateService.insert(insert);
                } else {
                    lastUpdate.setData(lastTransaction.getOpScore().toString());
                    lastUpdateService.update(lastUpdate);
                }
            } else {
                log.info("No transactions for " + token.getTick());
            }
        });
    }
}
