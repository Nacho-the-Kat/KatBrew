package com.katbrew.workflows.tasks.transactions;

import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.LastUpdate;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.entities.jooq.db.tables.pojos.Transaction;
import com.katbrew.services.tables.LastUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenerateLastUpdateTransactionsService {

    private final LastUpdateService lastUpdateService;
    private final DSLContext dslContext;

    public void execute(final Token token) {
        final LastUpdate lastUpdate = lastUpdateService.findByIdentifier("fetchTokenTransactions" + token.getTick());

        final String identifier = "fetchTokenTransactions" + token.getTick();
        final List<Transaction> lastTransactionList = dslContext
                .select(Tables.TRANSACTION.ID, Tables.TRANSACTION.MTS_ADD)
                .from(Tables.TRANSACTION)
                .where(Tables.TRANSACTION.FK_TOKEN.eq(token.getId()))
                .orderBy(Tables.TRANSACTION.MTS_ADD.desc())
                .limit(1)
                .fetch()
                .into(Transaction.class);
        if (!lastTransactionList.isEmpty()) {
            final Transaction lastTransaction = lastTransactionList.get(0);

            if (lastUpdate == null) {
                final LastUpdate insert = new LastUpdate();
                insert.setData(lastTransaction.getMtsAdd().toString());
                insert.setIdentifier(identifier);
                lastUpdateService.insert(insert);
            } else {
                lastUpdate.setData(lastTransaction.getMtsAdd().toString());
                lastUpdateService.update(lastUpdate);
            }
        } else {
            log.info("No transactions for " + token.getTick());
        }
    }
}
