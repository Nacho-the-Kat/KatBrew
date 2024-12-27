package com.katbrew.services.tables;

import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.daos.TokenDao;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.exceptions.NotValidException;
import com.katbrew.services.base.JooqService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TokenService extends JooqService<Token, TokenDao> {

    public List<Token> getTokens() {
        return this.findAll();
    }

    public List<String> getTickers() {
        return this.findAll().stream().map(Token::getTick).collect(Collectors.toList());
    }

    public List<Token> getTokenList(final Integer limit, final Integer cursor, final String sortBy, final String sortOrder) {
        if (sortBy.contains(";") || sortBy.contains(",")) {
            throw new NotValidException();
        }
        if (cursor == null || limit == null) {
            return this.findAllSorted(sortBy, sortOrder);
        }
        return this.findBySortedLimitOffset(limit, cursor, sortBy, sortOrder);
    }

    public Token findByTick(final String tick) {
        List<Token> token = this.findBy(Collections.singletonList(Tables.TOKEN.TICK.eq(tick)));
        if (token.isEmpty()) {
            return null;
        }
        return token.get(0);
    }

    public List<Token> findByTicks(final List<String> ticks) {
        return this.findBy(Collections.singletonList(Tables.TOKEN.TICK.in(ticks)));
    }
}
