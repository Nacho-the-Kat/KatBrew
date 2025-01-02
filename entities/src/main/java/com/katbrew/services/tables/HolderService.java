package com.katbrew.services.tables;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.daos.HolderDao;
import com.katbrew.entities.jooq.db.tables.pojos.Holder;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.pojos.TopHolder;
import com.katbrew.services.base.JooqService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HolderService extends JooqService<Holder, HolderDao> {

    private final TokenService tokenService;
    private final ObjectMapper objectMapper = KatBrewObjectMapper.createObjectMapper();
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

    public List<TopHolder> getTopHolders() {
        final com.katbrew.entities.jooq.db.tables.Holder holder = Tables.HOLDER;
        final com.katbrew.entities.jooq.db.tables.Token token = Tables.TOKEN;
        final com.katbrew.entities.jooq.db.tables.Balance balance = Tables.BALANCE;
        final Result<Record> dbEntries = context.select()
                .from(holder)
                .join(balance).on(holder.ID.eq(balance.HOLDER_ID))
                .join(token).on(balance.FK_TOKEN.eq(token.ID))
                .fetch();
        final HashMap<String, TopHolder> map = new HashMap<>();
        dbEntries.forEach(single -> {
            TopHolder topHolder;
            if (map.containsKey(single.get(holder.ADDRESS))) {
                topHolder = map.get(single.get(holder.ADDRESS));
            } else {
                topHolder = new TopHolder(
                        single.get(holder.ADDRESS),
                        new ArrayList<>()
                );
                map.put(single.get(holder.ADDRESS), topHolder);
            }
            topHolder.getBalances().add(new TopHolder.Balances(
                    single.get(token.TICK),
                    single.get(balance.BALANCE_)
            ));

        });
        final List<TopHolder> topHolders = new ArrayList<>(map.values());
        topHolders.sort(Comparator.comparingInt(single -> single.getBalances().size()));
        Collections.reverse(topHolders);
        return topHolders;

    }

    @Data
    public static class TickHolder {
        String tick;
        Integer holderTotal;
    }
}
