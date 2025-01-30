package com.katbrew.workflows.tasks.nfts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollection;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollectionEntry;
import com.katbrew.helper.KatBrewWebClient;
import com.katbrew.helper.NftHelper;
import com.katbrew.pojos.NFTCollectionEntryInternal;
import com.katbrew.services.tables.NFTCollectionEntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchNFTSEntries implements JavaDelegate {

    @Value("${data.fetchNFT.collection.entries.baseUrl}")
    private String fetchBaseUrl;

    private final NFTCollectionEntryService nftCollectionEntryService;
    private final DSLContext dsl;
    private final WebClient client = KatBrewWebClient.createWebClient();
    private final NftHelper nftHelper = new NftHelper();


    @Override
    public void execute(DelegateExecution execution) throws JsonProcessingException {
        log.info("Starting the nft entry sync:" + LocalDateTime.now());

        //we search for all Infos, because we fetch the info AFTER the entries, because then we can look, which nft entries we dont fetched yet
        final List<BigInteger> nftCollectionInfoFK = dsl.select(Tables.NFT_COLLECTION_INFO.FK_COLLECTION)
                .from(Tables.NFT_COLLECTION_INFO)
                .fetch()
                .into(BigInteger.class);

        final List<NftCollection> nfts = dsl.selectFrom(Tables.NFT_COLLECTION)
                .where(Tables.NFT_COLLECTION.ID.notIn(nftCollectionInfoFK))
                .fetch()
                .into(NftCollection.class);

        try {
            nfts.forEach(nft -> {
                log.info("Starting the nft entry sync for: " + nft.getTick());
                final ExecutorService executor = Executors.newFixedThreadPool(Math.min(nft.getMax().intValue(), 100));
                final List<NftCollectionEntry> list = new ArrayList<>();
                for (int i = 1; i < nft.getMax().intValue() +1; i++) {
                    final AtomicInteger atomicInteger = new AtomicInteger(i);
                    executor.submit(() -> {
                        long time = (long) (Math.random() * 1000);
                        long startZeit = System.currentTimeMillis();
                        while (System.currentTimeMillis() - startZeit < time) {
                            //empty while
                        }
                        if (nft.getBuri() != null) {
                            String uri = fetchBaseUrl.replace("{ticker}", nft.getTick()).replace("{id}", atomicInteger.toString());
                            try {

                                final NFTCollectionEntryInternal result = client
                                        .get()
                                        .uri(uri)
                                        .retrieve()
                                        .bodyToMono(NFTCollectionEntryInternal.class)
                                        .retry(3)
                                        .block();

                                if (result != null) {
                                    NftCollectionEntry info;
                                    try {
                                        info = nftHelper.convertEntryToDbEntry(result);
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                    info.setFkCollection(nft.getId());
                                    list.add(info);
                                }
                            } catch (Exception e) {
                                log.error(e.getMessage());
                            }
                        }
                    });
                }
                executor.shutdown();
                try {
                    executor.awaitTermination(60, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                nftCollectionEntryService.batchInsert(list);
            });
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        log.info("Finished the nft entry sync:" + LocalDateTime.now());
    }
}
