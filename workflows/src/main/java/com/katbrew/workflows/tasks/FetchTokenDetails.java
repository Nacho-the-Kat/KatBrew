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
import java.util.Collections;
import java.util.List;

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
        List<Token> tokenList = tokenService.findAll();
        log.info("Starting the token detail sync");

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
            tokenService.update(internal);

            List<Balance> dbBalances = balanceService.findBy(Collections.singletonList(Tables.BALANCE.FK_TOKEN.eq(token.getId())));
            List<Holder> dbHolder = holderService.findBy(Collections.singletonList(Tables.HOLDER.ID.in(dbBalances.stream().map(Balance::getHolderId).toList())));

            List<HolderInternal> holderInternals = responseTokenList.getResult().get(0).getHolder();
            if (holderInternals != null) {
                responseTokenList.getResult().get(0).getHolder().forEach(singleHolder -> {
                    Holder intern = dbHolder.stream().filter(internEntry -> internEntry.getAddress().equals(singleHolder.getAddress())).findFirst().orElse(null);
                    if (intern == null) {
                        List<Holder> addressHolder = holderService.findBy(Collections.singletonList(Tables.HOLDER.ADDRESS.eq(singleHolder.getAddress())));
                        if (!addressHolder.isEmpty()) {
                            intern = addressHolder.get(0);
                        }
                    }
                    //completely new holder
                    if (intern == null) {
                        Holder newHolder = new Holder();
                        newHolder.setAddress(singleHolder.getAddress());
                        newHolder = holderService.insert(newHolder);
                        createNewBalance(newHolder, singleHolder.getAmount(), token);
                    } else {
                        final List<Balance> balance = balanceService.findBy(List.of(Tables.BALANCE.HOLDER_ID.eq(intern.getId()), Tables.BALANCE.FK_TOKEN.eq(token.getId())));
                        if (!balance.isEmpty()) {
                            //we have a balance for the holder, update it
                            Balance oldOne = balance.get(0);
                            oldOne.setBalance(singleHolder.getAmount());
                            balanceService.update(oldOne);
                        } else {
                            //new balance for an old holder
                            createNewBalance(intern, singleHolder.getAmount(), token);
                        }
                    }
                });
                List<Balance> updatedBalances = balanceService.findBy(Collections.singletonList(Tables.BALANCE.FK_TOKEN.eq(token.getId())));
                List<BigInteger> holders = holderService.findBy(List.of(Tables.HOLDER.ADDRESS.in(responseTokenList.getResult().get(0).getHolder().stream().map(HolderInternal::getAddress).toList())))
                        .stream().map(Holder::getId).toList();
                updatedBalances.forEach(singleBalance -> {
                    if (!holders.contains(singleBalance.getHolderId())) {
                        //holder id of the balance entry is not in the response, delete it
                        balanceService.delete(singleBalance);
                    }
                });
            }
        });
    }

    private void createNewBalance(final Holder holder, final BigInteger amount, final Token token) {
        final Balance balance = new Balance();
        balance.setBalance(amount);
        balance.setHolderId(holder.getId());
        balance.setFkToken(token.getId());
        balanceService.insert(balance);
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
