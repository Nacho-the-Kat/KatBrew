package com.nacho.services.tables;

import com.nacho.entities.jooq.db.tables.daos.TokenDao;
import com.nacho.entities.jooq.db.tables.pojos.Token;
import com.nacho.entities.jooq.db.tables.records.TokenRecord;
import com.nacho.exceptions.NotValidException;
import com.nacho.services.base.AbstractJooqService;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TokensService extends AbstractJooqService<Token, TokenRecord> {

    private final JdbcTemplate jdbcTemplate;

    public TokensService(final DSLContext configuration) {
        super(new TokenDao(), new TokenRecord(), Token.class, configuration);
        this.jdbcTemplate = new JdbcTemplate(configuration.dsl().parsingDataSource());
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
}
