package com.katbrew.workflows.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.TopHolder;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.pojos.TopHolderBalance;
import com.katbrew.pojos.TopHolderGenerated;
import com.katbrew.services.tables.TopHolderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenerateTopHolders implements JavaDelegate {

    private final TopHolderService topHolderService;
    private final DSLContext context;
    private final ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    @Override
    public void execute(DelegateExecution execution) {
        final Map<String, TopHolder> dbHolder = topHolderService.findAll().stream().collect(Collectors.toMap(TopHolder::getAddress, single -> single));

        final com.katbrew.entities.jooq.db.tables.Holder holder = Tables.HOLDER;
        final com.katbrew.entities.jooq.db.tables.Token token = Tables.TOKEN;
        final com.katbrew.entities.jooq.db.tables.Balance balance = Tables.BALANCE;
        final Result<Record> dbEntries = context.select()
                .from(holder)
                .join(balance).on(holder.ID.eq(balance.HOLDER_ID))
                .join(token).on(balance.FK_TOKEN.eq(token.ID))
                .fetch();

        final HashMap<String, TopHolderGenerated> map = new HashMap<>();

        dbEntries.forEach(single -> {
            TopHolderGenerated topHolder;
            if (map.containsKey(single.get(holder.ADDRESS))) {
                topHolder = map.get(single.get(holder.ADDRESS));
            } else {
                topHolder = new TopHolderGenerated();
                topHolder.setAddress(single.get(holder.ADDRESS));
                topHolder.setTokenCount(0);
                final TopHolder db = dbHolder.get(holder.ADDRESS);
                if (db != null) {
                    topHolder.setId(db.getId());
                }
                topHolder.setBalances(new ArrayList<>());
                map.put(single.get(holder.ADDRESS), topHolder);
            }
            final TopHolderBalance topHolderBalance = new TopHolderBalance();
            topHolderBalance.setTick(single.get(token.TICK));
            topHolderBalance.setAmount(single.get(balance.BALANCE_));
            topHolder.getBalances().add(topHolderBalance);
            topHolder.setTokenCount(topHolder.getBalances().size());

        });
        final List<TopHolderGenerated> topHolders = new ArrayList<>(map.values());
        topHolders.sort(Comparator.comparingInt(TopHolderGenerated::getTokenCount));
        Collections.reverse(topHolders);
        List<TopHolder> right = topHolders.stream().map(single -> {
            TopHolder topHolder = dbHolder.get(single.getAddress());
            if (topHolder == null) {
                topHolder = new TopHolder();
                topHolder.setAddress(single.getAddress());
            }
            topHolder.setTokenCount(single.getTokenCount());
            try {
                topHolder.setBalances(mapper.writeValueAsString(single.getBalances()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return topHolder;
        }).toList();
        topHolderService.batchInsert(right.stream().filter(single -> single.getId() == null).toList());
        topHolderService.batchUpdate(right.stream().filter(single -> single.getId() != null).toList());
    }


}
