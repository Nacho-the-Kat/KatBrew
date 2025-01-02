package com.katbrew.workflows.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.Balance;
import com.katbrew.entities.jooq.db.tables.pojos.Holder;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.services.tables.BalanceService;
import com.katbrew.services.tables.HolderService;
import com.katbrew.services.tables.TokenService;
import com.katbrew.workflows.helper.ParsingResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchTokenDetails implements JavaDelegate {
    ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    @Value("${data.fetchTokenBaseUrl}")
    private String tokenUrl;

    private final TokenService tokenService;
    private final HolderService holderService;
    private final BalanceService balanceService;

    //todo switch after full sync to timed every hour -> 0 0 * * * ?
    @Override
    public void execute(DelegateExecution execution) {

        WebClient client = WebClient.builder().build();
        final List<Token> tokenList = tokenService.findAll();
        log.info("Starting the token detail sync");
        final List<Token> updatedToken = new ArrayList<>();

        final Map<String, Holder> dbHolder = holderService.findAll().stream().collect(Collectors.toMap(Holder::getAddress, single -> single));
        final Map<String, BigInteger> addressId = dbHolder.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, single -> single.getValue().getId()));

        final HashMap<String, Holder> holderToCreate = new HashMap<>();
        final HashMap<String, List<Balance>> addressBalances = new HashMap<>();

        tokenList.forEach(token -> {
            ParsingResponse<List<ExtendedTokenData>> responseTokenList = client
                    .get()
                    .uri(tokenUrl + "/krc20/token/" + token.getTick())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ParsingResponse<List<ExtendedTokenData>>>() {
                    })
                    .block();
            if (responseTokenList == null) {
                log.error("no data was loaded");
                return;
            }
            Token internal = mapper.convertValue(responseTokenList.getResult().get(0), Token.class);
            internal.setId(token.getId());
            internal.setLogo(token.getLogo());
            updatedToken.add(internal);

            final Map<String, Balance> dbBalances = balanceService.findBy(List.of(Tables.BALANCE.FK_TOKEN.eq(token.getId())))
                    .stream().collect(Collectors.toMap(single -> single.getHolderId() + "-" + single.getFkToken(), single -> single));

            final List<HolderInternal> holderInternals = responseTokenList.getResult().get(0).getHolder();

            if (holderInternals != null) {
                holderInternals.forEach(singleHolder -> {
                    final String address = singleHolder.getAddress();
                    Holder intern = dbHolder.get(address);
                    //completely new holder
                    if (intern == null) {
                        if (!holderToCreate.containsKey(address)) {
                            Holder newHolder = new Holder();
                            newHolder.setAddress(address);
                            holderToCreate.put(address, newHolder);
                        }
                        addressBalances.put(address, List.of(createNewBalance(singleHolder.getAmount(), token)));
                    } else {
                        final Balance balance = dbBalances.get(addressId.get(address) + "-" + token.getId());
                        if (!addressBalances.containsKey(address)) {
                            addressBalances.put(address, new ArrayList<>());
                        }
                        if (balance != null) {
                            //we have a balance for the holder, update it
                            balance.setBalance(singleHolder.getAmount());
                            addressBalances.get(address).add(balance);
                        } else {
                            //new balance for an old holder
                            final Balance b = createNewBalance(singleHolder.getAmount(), token);
                            b.setHolderId(intern.getId());
                            addressBalances.get(address).add(b);
                        }
                    }
                });
//                List<Balance> updatedBalances = balanceService.findBy(Collections.singletonList(Tables.BALANCE.FK_TOKEN.eq(token.getId())));
//                List<BigInteger> holders = holderService.findBy(List.of(Tables.HOLDER.ADDRESS.in(responseTokenList.getResult().get(0).getHolder().stream().map(HolderInternal::getAddress).toList())))
//                        .stream().map(Holder::getId).toList();
//                updatedBalances.forEach(singleBalance -> {
//                    if (!holders.contains(singleBalance.getHolderId())) {
//                        //holder id of the balance entry is not in the response, delete it
//                        balanceService.delete(singleBalance);
//                    }
//                });
            }
        });
        tokenService.batchUpdate(updatedToken);
        holderService.batchInsert(holderToCreate.values().stream().toList()).forEach(single -> {
            dbHolder.put(single.getAddress(), single);
            addressId.put(single.getAddress(), single.getId());
        });
        addressBalances.forEach((key, value) -> value.forEach(singleB -> {
            singleB.setHolderId(addressId.get(key));
        }));
        List<Balance> toUpdate = addressBalances.values().stream().flatMap(single -> single.stream().filter(singleB -> singleB.getId() != null)).toList();
        List<Balance> newBalances = balanceService.insert(addressBalances.values().stream().flatMap(single -> single.stream().filter(singleB -> singleB.getId() == null)).toList());

        //todo andere löschen
        balanceService.update(toUpdate);
    }

    private Balance createNewBalance(final BigInteger amount, final Token token) {
        final Balance balance = new Balance();
        balance.setBalance(amount);
        balance.setFkToken(token.getId());
        return balance;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    private static class ExtendedTokenData extends Token {
        List<HolderInternal> holder;
    }

    @Data
    private static class HolderInternal {
        String address;
        BigInteger amount;
    }
}
