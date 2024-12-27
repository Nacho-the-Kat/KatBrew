package com.katbrew.services.tables;


import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.daos.TransactionDao;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.entities.jooq.db.tables.pojos.Transaction;
import com.katbrew.services.base.JooqService;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService extends JooqService<Transaction, TransactionDao> {

    private final TokenService tokenService;

    public List<Transaction> getTransactions(final String tick, final LocalDateTime start, LocalDateTime end) {
        final Token token = tokenService.findByTick(tick);
        if (token == null) {
            //todo throw ?
            return new ArrayList<>();
        }
        final List<Condition> conditions = List.of(
//                Tables.TRANSACTION.TIMESTAMP.ge(start),
//                Tables.TRANSACTION.TIMESTAMP.le(end),
                Tables.TRANSACTION.FK_TOKEN.eq(token.getId())
        );
        return this.findBy(conditions);
    }
}
