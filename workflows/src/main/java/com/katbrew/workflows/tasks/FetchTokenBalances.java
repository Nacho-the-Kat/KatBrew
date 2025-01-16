package com.katbrew.workflows.tasks;

import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.Balance;
import com.katbrew.entities.jooq.db.tables.pojos.Holder;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.helper.KatBrewHelper;
import com.katbrew.pojos.TransactionExternal;
import com.katbrew.services.tables.BalanceService;
import com.katbrew.services.tables.HolderService;
import com.katbrew.services.tables.TokenService;
import com.katbrew.workflows.helper.ParsingResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchTokenBalances implements JavaDelegate {

    @Value("${data.fetchTokenBaseUrl}")
    private String tokenUrl;

    private final TokenService tokenService;
    private final HolderService holderService;
    private final BalanceService balanceService;

    @Override
    public void execute(DelegateExecution execution) throws InterruptedException {
        log.info("Starting the token balances sync:" + LocalDateTime.now());
        final Map<String, Integer> tokenMap = tokenService.findAll().stream().collect(Collectors.toMap(Token::getTick, Token::getId));

        final Map<String, Holder> dbHolder = holderService.findAll().stream().collect(Collectors.toMap(Holder::getAddress, single -> single));
        final Map<String, BigInteger> addressId = dbHolder.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, single -> single.getValue().getId()));
        final Map<String, Balance> dbBalances = balanceService.findAll().stream().collect(Collectors.toMap(single -> single.getHolderId() + "-" + single.getFkToken(), single -> single));

        final ParameterizedTypeReference<ParsingResponse<List<HolderInternal>>> reference = new ParameterizedTypeReference<>() {
        };
        final KatBrewHelper<ParsingResponse<List<HolderInternal>>, TransactionExternal> helper = new KatBrewHelper<>(null);

        final Map<String, Holder> holderToCreate = new HashMap<>();
        final Map<String, List<Balance>> addressBalances = new HashMap<>();

        final ParsingResponse<List<HolderInternal>> responseTokenList = helper
                .fetch(
                        tokenUrl + "/addresses/balances",
                        reference
                );
        if (responseTokenList == null) {
            log.error("no balances were loaded");
            return;
        }

        final List<HolderInternal> holderInternals = responseTokenList.getResult();

        if (holderInternals != null) {
            holderInternals.forEach(singleHolder -> {
                final String address = singleHolder.getAddress();
                Holder intern = dbHolder.get(address);
                singleHolder.getBalances().forEach(singleBalance -> {
                    Integer tokenId = tokenMap.get(singleBalance.getTick());
                    if (tokenId == null) {
                        Token newToken = new Token();
                        newToken.setTick(singleBalance.getTick());
                        newToken = tokenService.insert(newToken);
                        tokenId = newToken.getId();
                        tokenMap.put(newToken.getTick(), newToken.getId());
                    }
                    //completely new holder
                    if (intern == null) {
                        if (!holderToCreate.containsKey(address)) {
                            Holder newHolder = new Holder();
                            newHolder.setAddress(address);
                            holderToCreate.put(address, newHolder);
                        }
                        addressBalances.put(address, List.of(createNewBalance(singleBalance.getBalance(), tokenId)));
                    } else {
                        final Balance balance = dbBalances.get(addressId.get(address) + "-" + tokenId);
                        if (!addressBalances.containsKey(address)) {
                            addressBalances.put(address, new ArrayList<>());
                        }
                        if (balance != null) {
                            //we have a balance for the holder, update it
                            balance.setBalance(singleBalance.getBalance());
                            addressBalances.get(address).add(balance);
                        } else {
                            //new balance for an old holder
                            final Balance b = createNewBalance(singleBalance.getBalance(), tokenId);
                            b.setHolderId(intern.getId());
                            addressBalances.get(address).add(b);
                        }
                    }
                });
            });
        }

        holderService.batchInsert(holderToCreate.values().stream().toList()).forEach(single -> {
            dbHolder.put(single.getAddress(), single);
            addressId.put(single.getAddress(), single.getId());
        });

        addressBalances.forEach((key, value) -> value.forEach(singleB -> singleB.setHolderId(addressId.get(key))));

        final List<Balance> toUpdate = addressBalances.values().stream().flatMap(single -> single.stream().filter(singleB -> singleB.getId() != null)).toList();
        final List<Balance> newBalances = balanceService.batchInsert(addressBalances.values().stream().flatMap(single -> single.stream().filter(singleB -> singleB.getId() == null)).toList());
        balanceService.batchUpdate(toUpdate);
        final List<Balance> combined = Stream.concat(toUpdate.stream(), newBalances.stream()).toList();
        final List<Balance> toDelete = balanceService.findBy(List.of(
                Tables.BALANCE.ID.notIn(combined.stream().map(Balance::getId).toList())
        ));
        balanceService.batchDelete(toDelete);
        log.info("Updateing balances done: " + LocalDateTime.now());
    }

    private Balance createNewBalance(final BigInteger amount, final Integer tokenId) {
        final Balance balance = new Balance();
        balance.setBalance(amount);
        balance.setFkToken(tokenId);
        return balance;
    }

    @Data
    private static class HolderInternal {
        String address;
        List<Balances> balances;

        @Data
        private static class Balances {
            BigInteger balance;
            BigInteger locked;
            String tick;
        }
    }
}
