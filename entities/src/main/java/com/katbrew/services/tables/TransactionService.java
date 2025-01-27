package com.katbrew.services.tables;


import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.daos.TransactionDao;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.entities.jooq.db.tables.pojos.Transaction;
import com.katbrew.services.base.JooqService;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService extends JooqService<Transaction, TransactionDao> {

    private final TokenService tokenService;
    private final DSLContext context;

    public List<Transaction> getTransactions(final String tick, final LocalDateTime start, LocalDateTime end) {
        final Token token = tokenService.findByTick(tick);
        if (token == null) {
            //todo throw ?
            return new ArrayList<>();
        }
        final List<Condition> conditions = List.of(
                Tables.TRANSACTION.MTS_ADD.ge(BigInteger.valueOf(start.toEpochSecond(ZoneOffset.UTC))),
                Tables.TRANSACTION.MTS_ADD.le(BigInteger.valueOf(end.toEpochSecond(ZoneOffset.UTC))),
                Tables.TRANSACTION.FK_TOKEN.eq(token.getId())
        );
        return this.findBy(conditions);
    }

    public List getMintsTotal(LocalDateTime start, LocalDateTime end) {
        if (start == null) {
            start = LocalDateTime.now().minusDays(3);
        }
        if (end == null) {
            end = LocalDateTime.now();
        }
        com.katbrew.entities.jooq.db.tables.Transaction transaction = Tables.TRANSACTION;
        List<Field> coll = new ArrayList<>(List.of(transaction.fields()));
        coll.add(Tables.TOKEN.TICK);

        return context.select(coll)
                .from(transaction)
                .join(Tables.TOKEN)
                .on(transaction.FK_TOKEN.eq(Tables.TOKEN.ID))
                .where(List.of(
                        transaction.MTS_ADD.ge(BigInteger.valueOf(start.toEpochSecond(ZoneOffset.UTC))),
                        transaction.MTS_ADD.le(BigInteger.valueOf(end.toEpochSecond(ZoneOffset.UTC))),
                        transaction.OP.eq("mint")
                ))
                .fetch()
                .intoMaps();
    }
}
