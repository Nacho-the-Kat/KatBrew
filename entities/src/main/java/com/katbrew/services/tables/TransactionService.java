package com.katbrew.services.tables;


import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.daos.TransactionDao;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.entities.jooq.db.tables.pojos.Transaction;
import com.katbrew.entities.jooq.db.tables.records.TransactionRecord;
import com.katbrew.services.base.JooqService;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService extends JooqService<Transaction, TransactionRecord> {

    private final TokenService tokenService;

    public TransactionService(
            final DSLContext dsl,
            final TokenService tokenService
    ) {
        super(new TransactionDao(), dsl);
        this.tokenService = tokenService;
    }

    public List<Transaction> getTransactions(final String tick, final LocalDateTime start, LocalDateTime end) {
        final Token token = tokenService.findByTick(tick);
        if (token == null) {
            //todo throw ?
            return new ArrayList<>();
        }
        final List<Condition> conditions = List.of(
                Tables.PRICE_DATA.TIMESTAMP.ge(start),
                Tables.PRICE_DATA.TIMESTAMP.le(end),
                Tables.PRICE_DATA.FK_TOKEN.eq(token.getId())
        );
        return this.findBy(conditions);
    }
}
