package com.katbrew.workflows.tasks.nfts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollection;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollectionEntry;
import com.katbrew.helper.KatBrewObjectMapper;
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
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchNFTSEntries implements JavaDelegate {

    @Value("${data.fetchNFT.collection.entries.baseUrl}")
    private String fetchBaseUrl;
    private final ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    private final NFTCollectionEntryService nftCollectionEntryService;
    private final DSLContext dsl;
    private final WebClient client = KatBrewWebClient.createRedirectWebClient();
    private final NftHelper nftHelper = new NftHelper();
    final List<String> urls = List.of("https://w3s.link/", "https://dweb.link/", "https://ipfs.io/");
    final Random random = new Random();


    @Override
    public void execute(DelegateExecution execution) throws JsonProcessingException {
        log.info("Starting the nft entry sync:" + LocalDateTime.now());


        final List<BigInteger> newCollectionIds = mapper.convertValue(execution.getVariable("newCollectionIds"), new TypeReference<>() {
        });

        final List<NftCollection> nfts = dsl.selectFrom(Tables.NFT_COLLECTION)
                .where(Tables.NFT_COLLECTION.ID.in(newCollectionIds))
                .fetch()
                .into(NftCollection.class);
        final ExecutorService executor = Executors.newFixedThreadPool(3);
        final List<String> endpointInUse = new ArrayList<>();
        try {
            nfts.forEach(nft -> {
                final String prefix = urls.stream().filter(single->!endpointInUse.contains(single)).toList().get(0);
                endpointInUse.add(prefix);
                if (nft.getBuri() != null) {
                    executor.submit(() -> {
                        log.info("Starting the nft entry sync for: " + nft.getTick());
                        final List<NftCollectionEntry> list = new ArrayList<>();
                        for (int i = 1; i < nft.getMax().intValue() + 1; i++) {
                            long time = (long) (Math.random() * 1000);
                            long startZeit = System.currentTimeMillis();
                            while (System.currentTimeMillis() - startZeit < time) {
                                //empty while
                            }
                            if (nft.getBuri() != null) {
                                String uri = prefix + fetchBaseUrl.replace("{buri}", nft.getBuri().replace("ipfs://", "")).replace("{id}", String.valueOf(i));
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
                        }
                        nftCollectionEntryService.batchInsert(list);
                    });
                }
            });
            executor.shutdown();
            try {
                executor.awaitTermination(60, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        log.info("Finished the nft entry sync:" + LocalDateTime.now());
    }
}
