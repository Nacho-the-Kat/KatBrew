package com.katbrew.workflows.tasks.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.FetchData;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.entities.jooq.db.tables.pojos.Transaction;
import com.katbrew.services.tables.CodeWordingsService;
import com.katbrew.services.tables.FetchDataService;
import com.katbrew.services.tables.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
@RequiredArgsConstructor
public class RepairTransactionCursors implements JavaDelegate {
    private final FetchDataService fetchDataService;
    private final CodeWordingsService codeWordingsService;
    private final TokenService tokenService;
    public final DSLContext dsl;

    @Override
    public void execute(DelegateExecution execution) throws JsonProcessingException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            final List<Token> tokens = tokenService.findAll();
            final Map<String, Integer> codes = codeWordingsService.getAsMapWithNull();

            for (final Token token : tokens) {
                log.info(token.getTick());
//                final List<Transaction> lastTransactionList = dsl
//                        .select(Tables.TRANSACTION.ID, Tables.TRANSACTION.OP_SCORE)
//                        .from(Tables.TRANSACTION)
//                        .where(Tables.TRANSACTION.FK_TOKEN.eq(token.getId()))
//                        .orderBy(Tables.TRANSACTION.OP_SCORE.desc())
//                        .limit(1)
//                        .fetch()
//                        .into(Transaction.class);
//                if (!lastTransactionList.isEmpty()) {
//                    final Transaction lastTransaction = lastTransactionList.get(0);
//
//                    final String identifier = "fetchTokenTransactionsLastCursor" + token.getTick();
//                    final FetchData lastUpdate = fetchDataService.findByIdentifier(identifier);
//
//                    if (lastUpdate == null) {
//                        final FetchData insert = new FetchData();
//                        insert.setData(lastTransaction.getOpScore().toString());
//                        insert.setIdentifier(identifier);
//                        fetchDataService.insert(insert);
//                    } else {
//                        lastUpdate.setData(lastTransaction.getOpScore().toString());
//                        fetchDataService.update(lastUpdate);
//                    }
//                } else {
//                    log.info("No transactions found");
//                }
                final BigInteger transferAmount = dsl
                        .select(DSL.count())
                        .from(Tables.TRANSACTION)
                        .where(Tables.TRANSACTION.FK_TOKEN.eq(token.getId()).and(Tables.TRANSACTION.OP.eq(codes.get("transfer"))).and(Tables.TRANSACTION.OP_ERROR.isNull()))
                        .limit(1)
                        .fetch()
                        .get(0)
                        .into(BigInteger.class);
                final BigInteger mintAmount = dsl
                        .select(DSL.count())
                        .from(Tables.TRANSACTION)
                        .where(Tables.TRANSACTION.FK_TOKEN.eq(token.getId()).and(Tables.TRANSACTION.OP.eq(codes.get("mint"))).and(Tables.TRANSACTION.OP_ERROR.isNull()))
                        .limit(1)
                        .fetch()
                        .get(0)
                        .into(BigInteger.class);
                token.setTransferTotal(transferAmount);
                token.setMintTotal(mintAmount.intValue());
                tokenService.update(token);
                log.info(token.getTick() + " finished");
            }
        });
        executorService.shutdown();
    }
}
