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

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HolderService extends JooqService<Holder, HolderDao> {

    private final TokenService tokenService;
    private final TopHolderService topHolderService;
    public final ObjectMapper objectMapper = KatBrewObjectMapper.createObjectMapper();

    public Holder findByAddress(final String address) {
        final List<Holder> holder = this.findBy(Collections.singletonList(Tables.HOLDER.ADDRESS.eq(address)));
        if (holder.isEmpty()) {
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

    //Parsing in the frontend
    public List<TopHolderService.TopHolderResponse> getTopHolders() {
        return topHolderService.getTopHolders();
    }

    public ConcurrentMap<String, BigInteger> getAddressIdMap() {
        return findAll().stream().collect(Collectors.toConcurrentMap(Holder::getAddress, Holder::getId));
    }

    @Data
    public static class TickHolder {
        String tick;
        Integer holderTotal;
    }
}
