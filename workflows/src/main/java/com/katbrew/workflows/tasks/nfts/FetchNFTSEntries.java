package com.katbrew.workflows.tasks.nfts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollection;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.services.tables.LastUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchNFTSEntries implements JavaDelegate {

    private final NftFetchHelper nftFetchHelper;
    private final DSLContext dsl;
    private final LastUpdateService lastUpdateService;
    private final ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    @Override
    public void execute(DelegateExecution execution) throws JsonProcessingException {
        final List<BigInteger> newCollectionIds = mapper.convertValue(execution.getVariable("newCollectionIds"), new TypeReference<>() {
        });

        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            log.info("Starting the nft entry sync:" + LocalDateTime.now());
            final List<NftCollection> nfts = dsl.selectFrom(Tables.NFT_COLLECTION)
                    .where(Tables.NFT_COLLECTION.ID.in(newCollectionIds))
                    .fetch()
                    .into(NftCollection.class);

            for (final NftCollection single : nfts) {
                nftFetchHelper.fetch(single);
            }
            log.info("done with generating nft entry and collection infos");
            lastUpdateService.releaseTask("fetchNFT");
        });
        executorService.shutdown();
    }

}
