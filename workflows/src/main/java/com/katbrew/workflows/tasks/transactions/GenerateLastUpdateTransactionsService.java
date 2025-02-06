package com.katbrew.workflows.tasks.transactions;

import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.FetchData;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.entities.jooq.db.tables.pojos.Transaction;
import com.katbrew.services.tables.FetchDataService;
import com.katbrew.services.tables.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenerateLastUpdateTransactionsService {

    private final FetchDataService fetchDataService;
    private final TokenService tokenService;
    private final DSLContext dslContext;

    public void execute() {
        final List<Token> tokens = tokenService.findAll();

        for (final Token token : tokens) {
            final String identifier = "fetchTransactionsLastCursor" + token.getTick();
            final FetchData lastUpdate = fetchDataService.findByIdentifier(identifier);

            final List<Transaction> lastTransactionList = dslContext
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
