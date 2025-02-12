package com.katbrew.services.tables;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.daos.TokenDao;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.exceptions.NotValidException;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.pojos.TokenHolder;
import com.katbrew.pojos.TokenList;
import com.katbrew.services.base.JooqService;
import com.katbrew.services.helper.TokenCachingService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenService extends JooqService<Token, TokenDao> {

    public final TokenCachingService tokenCachingService;
    private final ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();
    private final DSLContext context;

    public List<String> getTickers() {
        if (tokenCachingService.hasTokenTicks()) {
            return tokenCachingService.getTokenTicks();
        }
        final List<String> tokenTicks = this.findAll().stream().map(Token::getTick).toList();
        tokenCachingService.renewTokenTicks(tokenTicks);
        return tokenTicks;
    }

    public void updateSocials(final String tick, final Map<String, String> socials) throws NotFoundException, JsonProcessingException {
        final Token token = findByTick(tick.toUpperCase());
        if (token == null) {
            throw new NotFoundException("token not found");
        }

        if (!socials.isEmpty()) {
            token.setSocials(mapper.writeValueAsString(socials));
            update(token);
        }
    }

    public List<TokenList> getTokenList(final String sortBy, final String sortOrder) {
        if (sortBy.contains(";") || sortBy.contains(",")) {
            throw new NotValidException();
        }
        if (tokenCachingService.hasTokenList()) {
            return tokenCachingService.getTokenList();
        }
        final List<TokenList> tokens = findAllSortedCustomType(
                sortBy,
                sortOrder,
                Arrays.stream(Tables.TOKEN.fields()).filter(single ->
                        !single.getName().equals("hash_rev") &&
                                !single.getName().equals("to") &&
                                !single.getName().equals("op_score_add") &&
                                !single.getName().equals("op_score_mod")
                ).toList()).into(TokenList.class);
        tokenCachingService.renewTokenList(tokens);
        return tokens;
    }

    public List<TokenHolder> getHolder(final String tick) throws NotFoundException {
        final Token checkToken = findByTick(tick);
        if (checkToken == null) {
            throw new NotFoundException("token not found");
        }
        if (tokenCachingService.hasTokenHolder(tick)) {
            return tokenCachingService.getTokenHolder(tick);
        }

        com.katbrew.entities.jooq.db.tables.Token token = Tables.TOKEN;
        final List<TokenHolder> holder = context.select(Tables.HOLDER.ADDRESS, Tables.BALANCE.BALANCE_)
                .from(token)
                .join(Tables.BALANCE).on(Tables.BALANCE.FK_TOKEN.eq(token.ID))
                .join(Tables.HOLDER).on(Tables.BALANCE.HOLDER_ID.eq(Tables.HOLDER.ID))
                .where(token.TICK.eq(tick))
                .orderBy(Tables.BALANCE.BALANCE_.cast(BigInteger.class).desc())
                .limit(50)
                .fetch()
                .into(TokenHolder.class);

        tokenCachingService.renewTokenHolder(tick, holder);
        return holder;
    }

    public List<Token> findByTicks(final List<String> ticks) {
        return this.findBy(Collections.singletonList(Tables.TOKEN.TICK.in(ticks)));
    }

    public Token findByTick(final String tick) {
        List<Token> token = this.findBy(Collections.singletonList(Tables.TOKEN.TICK.eq(tick)));
        if (token.isEmpty()) {
            return null;
        }
        return token.get(0);
    }
}
