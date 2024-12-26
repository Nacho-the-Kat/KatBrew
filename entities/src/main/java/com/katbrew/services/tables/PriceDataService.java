package com.katbrew.services.tables;

import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.daos.PriceDataDao;
import com.katbrew.entities.jooq.db.tables.pojos.PriceData;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.services.base.JooqService;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceDataService extends JooqService<PriceData, PriceDataDao> {
    private final TokenService tokenService;

    public List<PriceData> getTokenPriceData(final String tick, final LocalDateTime start, final LocalDateTime end) {

        final Token token = tokenService.findByTick(tick);
        if (token == null) {
            return new ArrayList<>();
        }
        final List<Condition> conditions = List.of(
                Tables.PRICE_DATA.TIMESTAMP.ge(start),
                Tables.PRICE_DATA.TIMESTAMP.le(end),
                Tables.PRICE_DATA.FK_TOKEN.eq(token.getId())
        );
        return this.findBy(conditions, Collections.singletonList(Tables.PRICE_DATA.TIMESTAMP.asc()));
    }
}
