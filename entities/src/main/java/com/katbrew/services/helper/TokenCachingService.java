package com.katbrew.services.helper;

import com.katbrew.pojos.TokenHolder;
import com.katbrew.pojos.TokenList;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class TokenCachingService {

    @Getter
    List<TokenList> tokenList = new ArrayList<>();

    @Getter
    List<String> tokenTicks = new ArrayList<>();
    final HashMap<String, List<TokenHolder>> tokenHolders = new HashMap<>();

    public void renewTokenList(final List<TokenList> newList) {
        tokenList.clear();
        tokenList = newList;
    }

    public boolean hasTokenList() {
        return !tokenList.isEmpty();
    }

    public void invalidateTokenList() {
        tokenList.clear();
    }

    public void renewTokenTicks(final List<String> ticks) {
        tokenTicks.clear();
        tokenTicks = ticks;
    }

    public boolean hasTokenTicks() {
        return !tokenTicks.isEmpty();
    }

    public void invalidateTicks() {
        tokenTicks.clear();
    }

    public List<TokenHolder> getTokenHolder(final String tick) {
        return tokenHolders.get(tick);
    }

    public void renewTokenHolder(final String token, final List<TokenHolder> holder) {
        tokenHolders.remove(token);
        tokenHolders.put(token, holder);
    }

    public boolean hasTokenHolder(final String token) {
        return !tokenHolders.isEmpty() && tokenHolders.containsKey(token);
    }

    public void invalidateHolder() {
        tokenHolders.clear();
    }

}
