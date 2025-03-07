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
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionService extends JooqService<Transaction, TransactionDao> {

    private final TokenService tokenService;
    private final CodeWordingsService codeWordingsService;
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

    public List getMintsTotal(final String start, final String end) {
        LocalDateTime startDate;
        LocalDateTime endDate;
        if (start == null) {
            startDate = LocalDateTime.now().minusDays(3);
        }else{
            startDate = LocalDateTime.parse(start);
        }
        if (end == null) {
            endDate = LocalDateTime.now();
        }else{
            endDate = LocalDateTime.parse(end);
        }
        endDate = endDate.toLocalDate().atTime(LocalTime.MAX);
        startDate = startDate.toLocalDate().atTime(LocalTime.MIN);
        com.katbrew.entities.jooq.db.tables.Transaction transaction = Tables.TRANSACTION;
        List<Field> coll = new ArrayList<>(List.of(transaction.ID, transaction.FK_TOKEN, transaction.OP));
        coll.add(Tables.TOKEN.TICK);
        Map<String, Integer> codes = codeWordingsService.getAsMapWithNull();
        Map<String, Integer> mints = new HashMap<>();
        final List<Map<String, Object>> mintList = context.select(coll)
                .from(transaction)
                .join(Tables.TOKEN)
                .on(transaction.FK_TOKEN.eq(Tables.TOKEN.ID))
                .where(List.of(
                        transaction.MTS_ADD.ge(BigInteger.valueOf(startDate.toInstant(ZoneOffset.UTC).toEpochMilli())),
                        transaction.MTS_ADD.le(BigInteger.valueOf(endDate.toInstant(ZoneOffset.UTC).toEpochMilli())),
                        transaction.OP.eq(codes.get("mint"))
                ))
                .orderBy(Tables.TRANSACTION.OP_SCORE.desc())
                .limit(50000)
                .fetch()
                .intoMaps();
        mintList.forEach(single->{
            final String tick = (String) single.get("tick");
            if (!mints.containsKey(tick)){
                mints.put(tick, 0);
            }
            mints.put(tick, mints.get(tick) +1);
        });
        return mints.entrySet().stream().map(single -> Map.of("tick", single.getKey(), "mintTotal", single.getValue())).toList();
    }
}
