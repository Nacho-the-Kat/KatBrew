package com.katbrew.services.tables;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.daos.HolderDao;
import com.katbrew.entities.jooq.db.tables.pojos.Holder;
import com.katbrew.entities.jooq.db.tables.records.HolderRecord;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.services.base.JooqService;
import lombok.Data;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HolderService extends JooqService<Holder, HolderRecord> {

    private final TokenService tokenService;
    private final ObjectMapper objectMapper = KatBrewObjectMapper.createObjectMapper();

    public HolderService(final DSLContext context, final TokenService tokenService) {
        super(new HolderDao(), context);
        this.tokenService = tokenService;
    }

    public List<TickHolder> getHolderData() {
        return objectMapper.convertValue(
                tokenService.findAll(List.of(Tables.TOKEN.TICK, Tables.TOKEN.HOLDER_TOTAL)),
                new TypeReference<>() {}
        );
    }

    @Data
    public static class TickHolder {
        String tick;
        Integer holderTotal;
    }
}
