package com.katbrew.workflows.tasks.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.FetchData;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.entities.jooq.db.tables.pojos.Transaction;
import com.katbrew.services.tables.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class RepairTransactionCursors implements JavaDelegate {
    private final FetchDataService fetchDataService;
    private final TokenService tokenService;
    public final DSLContext dsl;

    @Override
    public void execute(DelegateExecution execution) throws JsonProcessingException {
        final List<Token> tokens = tokenService.findAll();

        for (final Token token : tokens) {
            final String identifier = "fetchTokenTransactionsLastCursor" + token.getTick();
            final String safetyIdentifier = "fetchTransactionsSafetySave" + token.getTick();

            final FetchData lastUpdate = fetchDataService.findByIdentifier(identifier);
            final FetchData safetyLastUpdate = fetchDataService.findByIdentifier(safetyIdentifier);

            if (safetyLastUpdate != null && safetyLastUpdate.getData() == null) {
                //This data exists if the token would have start the sync

                final List<Transaction> lastTransactionList = dsl
                        .select(Tables.TRANSACTION.ID, Tables.TRANSACTION.OP_SCORE)
                        .from(Tables.TRANSACTION)
                        .where(Tables.TRANSACTION.FK_TOKEN.eq(token.getId()))
                        .orderBy(Tables.TRANSACTION.OP_SCORE.cast(BigInteger.class).desc())
                        .limit(1)
                        .fetch()
                        .into(Transaction.class);
                if (!lastTransactionList.isEmpty()) {
                    final Transaction lastTransaction = lastTransactionList.get(0);

                    if (lastUpdate == null) {
                        final FetchData insert = new FetchData();
                        insert.setData(lastTransaction.getOpScore().toString());
                        insert.setIdentifier(identifier);
                        fetchDataService.insert(insert);
                    } else {
                        lastUpdate.setData(lastTransaction.getOpScore().toString());
                        fetchDataService.update(lastUpdate);
                    }
                } else {
                    log.info("No transactions found");
                }
            }
        }
    }
}
