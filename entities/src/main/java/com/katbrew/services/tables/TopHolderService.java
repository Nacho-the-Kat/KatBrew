package com.katbrew.services.tables;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.tables.daos.TopHolderDao;
import com.katbrew.entities.jooq.db.tables.pojos.TopHolder;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.services.base.JooqService;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Service
public class TopHolderService extends JooqService<TopHolder, TopHolderDao> {

    List<TopHolderResponse> topHolders = new ArrayList<>();
    final ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();
    final static TypeReference<List<ResponseBalances>> typeReference = new TypeReference<>() {};

    @PostConstruct
    private void getTopHolder() {
        cacheNewHolder();
    }

    public void cacheNewHolder() {
        this.topHolders = this.findBySortedLimitOffset(10000, 0, "tokenCount", "desc")
                .stream().map(single -> {
                    final TopHolderResponse holder = new TopHolderResponse();
                    holder.setAddress(single.getAddress());
                    holder.setId(single.getId());
                    holder.setTokenCount(single.getTokenCount());
                    try {
                        holder.setBalances(mapper.readValue(single.getBalances(), typeReference));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    return holder;
                }).toList();
    }

    @Data
    public static class TopHolderResponse {
        BigInteger id;
        String address;
        Integer tokenCount;
        List<ResponseBalances> balances;
    }
    @Data
    public static class ResponseBalances {
        String tick;
        BigInteger amount;
    }
}
