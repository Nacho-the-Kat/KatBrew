package com.katbrew.services.tables;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.daos.HolderDao;
import com.katbrew.entities.jooq.db.tables.pojos.Holder;
import com.katbrew.entities.jooq.db.tables.pojos.TopHolder;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.pojos.TopHolderBalance;
import com.katbrew.pojos.TopHolderGenerated;
import com.katbrew.services.base.JooqService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.JSON;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.tools.json.JSONArray;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HolderService extends JooqService<Holder, HolderDao> {

    private final TokenService tokenService;
    private final TopHolderService topHolderService;
    public final ObjectMapper objectMapper = KatBrewObjectMapper.createObjectMapper();
    private final DSLContext context;

    public Holder findByAddress(final String address){
        final List<Holder> holder = this.findBy(Collections.singletonList(Tables.HOLDER.ADDRESS.eq(address)));
        if (holder.isEmpty()){
            return null;
        }
        return holder.get(0);
    }

    public List<TickHolder> getHolderData() {
        return objectMapper.convertValue(
                tokenService.findAll(List.of(Tables.TOKEN.TICK, Tables.TOKEN.HOLDER_TOTAL)),
                new TypeReference<>() {
                }
        );
    }

    public List getTopHolders() {
        List<TopHolder> intern = topHolderService.findAll();
        intern.sort(Comparator.comparingInt(TopHolder::getTokenCount));
        Collections.reverse(intern);
        return intern.stream().map(single-> {
            TopHolderGenerated generated = new TopHolderGenerated();
            generated.setId(single.getId());
            generated.setAddress(single.getAddress());
            generated.setTokenCount(single.getTokenCount());
            generated.setBalances(objectMapper.convertValue(single.getBalances(), new TypeReference<List<TopHolderBalance>>() {
            }));
            return generated;
        }).toList();
    }

    @Data
    public static class TickHolder {
        String tick;
        Integer holderTotal;
    }
}
