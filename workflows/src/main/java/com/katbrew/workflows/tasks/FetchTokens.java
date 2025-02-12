package com.katbrew.workflows.tasks;

import com.katbrew.entities.jooq.db.tables.pojos.LastUpdate;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.helper.KatBrewHelper;
import com.katbrew.services.helper.TokenCachingService;
import com.katbrew.services.tables.LastUpdateService;
import com.katbrew.services.tables.TokenService;
import com.katbrew.workflows.helper.ParsingResponsePagedNFT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchTokens implements JavaDelegate {

    @Value("${data.fetchTokenBaseUrl}")
    private String tokenUrl;

    private final TokenService tokenService;
    private final TokenCachingService tokenCachingService;
    private final LastUpdateService lastUpdateService;
    private final KatBrewHelper<ParsingResponsePagedNFT<List<Token>>, Token> client = new KatBrewHelper<>();

    private static String getCursor(ParsingResponsePagedNFT<List<Token>> entity) {
        return entity.getNext();
    }

    @Override
    public void execute(DelegateExecution execution) throws InterruptedException {

        log.info("Starting the token sync: " + LocalDateTime.now());
        final ParameterizedTypeReference<ParsingResponsePagedNFT<List<Token>>> tokenListReference = new ParameterizedTypeReference<>() {
        };
        LastUpdate lastUpdate = lastUpdateService.findByIdentifier("tokenFetchLastCursor");

        final List<Token> tokenList = client.fetchPaginatedWithoutSave(
                tokenUrl + "/tokenlist",
                lastUpdate != null ? lastUpdate.getData() : null,
                true,
                true,
                "?next=",
                tokenListReference,
                FetchTokens::getCursor,
                ParsingResponsePagedNFT::getResult
        );
        if (tokenList == null) {
            log.error("no token data was loaded");
            return;
        }

        final Map<String, Token> dbEntries = tokenService.findByTicks(tokenList.stream().map(Token::getTick).toList())
                .stream().collect(Collectors.toMap(Token::getTick, single -> single));
        final List<Token> insert = new ArrayList<>();
        final List<Token> toUpdate = new ArrayList<>();

        tokenList.forEach(single -> {
            final Token token = dbEntries.get(single.getTick());
            if (token != null) {
                single.setId(token.getId());
                single.setHolderTotal(token.getHolderTotal());
                single.setTransferTotal(token.getTransferTotal());
                single.setMintTotal(token.getMintTotal());
                single.setLogo(token.getLogo());
                single.setSocials(token.getSocials());
                if (!token.equals(single)) {
                    //check if the data is the same, then no need to update
                    toUpdate.add(single);
                }
            } else {
                insert.add(single);
            }
        });
        //Sorting by the creation time to get entries in the db as they created
        insert.sort(Comparator.comparing(Token::getMtsAdd));

        tokenService.batchInsertVoidWithSub(insert);
        tokenService.batchUpdate(toUpdate);
        if (!insert.isEmpty() || !toUpdate.isEmpty()) {
            tokenCachingService.invalidateTokenList();
        }

        if (lastUpdate == null) {
            lastUpdate = new LastUpdate();
            lastUpdate.setIdentifier("tokenFetchLastCursor");
            lastUpdate.setData(tokenList.get(tokenList.size() - 1).getOpScoreAdd().toString());
            lastUpdateService.insert(lastUpdate);
        } else {
            lastUpdate.setData(tokenList.get(tokenList.size() - 1).getOpScoreAdd().toString());
            lastUpdateService.update(lastUpdate);
        }

        log.info("Finished the token sync: " + LocalDateTime.now());
    }

}
