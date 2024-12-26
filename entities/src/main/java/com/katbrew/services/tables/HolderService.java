package com.katbrew.services.tables;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.daos.HolderDao;
import com.katbrew.entities.jooq.db.tables.pojos.Holder;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.services.base.JooqService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HolderService extends JooqService<Holder, HolderDao> {

    private final TokenService tokenService;
    private final ObjectMapper objectMapper = KatBrewObjectMapper.createObjectMapper();

    public List<TickHolder> getHolderData() {
        return objectMapper.convertValue(
                tokenService.findAll(List.of(Tables.TOKEN.TICK, Tables.TOKEN.HOLDER_TOTAL)),
                new TypeReference<>() {
                }
        );
    }

    @Data
    public static class TickHolder {
        String tick;
        Integer holderTotal;
    }
}
