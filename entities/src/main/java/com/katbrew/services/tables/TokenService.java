package com.katbrew.services.tables;

import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.daos.TokenDao;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.entities.jooq.db.tables.records.TokenRecord;
import com.katbrew.exceptions.NotValidException;
import com.katbrew.services.base.JooqService;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TokenService extends JooqService<Token, TokenRecord> {

    private final JdbcTemplate jdbcTemplate;

    public TokenService(final DSLContext context) {
        super(new TokenDao(), context);
        this.jdbcTemplate = new JdbcTemplate(context.dsl().parsingDataSource());
    }

    public List<Token> getTokens() {
        return this.findAll();
    }

    public List<String> getTickers() {
        return this.findAll().stream().map(Token::getTick).collect(Collectors.toList());
    }

    public List<Token> getTokenList(final Integer limit, final String cursor, final String sortBy, final String sortOrder) {
        if (sortBy.contains(";") || sortBy.contains(",")) {
            throw new NotValidException();
        }

        //todo get abstract
        String sql = "select * from \"Token\" order By \"" + sortBy + "\" " + sortOrder + " limit " + limit + " offset " + cursor;

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Token.class));
    }

    public Token findByTick(final String tick) {
        List<Token> token = this.findBy(Collections.singletonList(Tables.TOKEN.TICK.eq(tick)));
        if (token.isEmpty()) {
            return null;
        }
        return token.get(0);
    }
}
