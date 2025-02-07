package com.katbrew.workflows.tasks.nfts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollection;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo;
import com.katbrew.helper.KatBrewObjectMapper;
import com.katbrew.helper.KatBrewWebClient;
import com.katbrew.helper.NftHelper;
import com.katbrew.pojos.NFTCollectionInfoInternal;
import com.katbrew.services.tables.NFTCollectionInfoService;
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
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchNFTSInfo implements JavaDelegate {

    @Value("${data.fetchNFT.collection.info.baseUrl}")
    private String fetchBaseUrl;
    private final NFTCollectionInfoService nftCollectionInfoService;
    private final DSLContext dsl;
    private final ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();
    private final WebClient client = KatBrewWebClient.createWebClient();
    private final NftHelper nftHelper = new NftHelper();

    @Override
    public void execute(DelegateExecution execution) throws JsonProcessingException {
        log.info("Starting the nft info sync:" + LocalDateTime.now());

        final List<BigInteger> newCollectionIds = mapper.convertValue(execution.getVariable("newCollectionIds"), new TypeReference<>() {
        });
        final List<NftCollection> nfts = dsl.selectFrom(Tables.NFT_COLLECTION)
                .where(Tables.NFT_COLLECTION.ID.in(newCollectionIds))
                .fetch()
                .into(NftCollection.class);

        nfts.forEach(nft -> {
            try {
                if (nft.getBuri() != null) {
                    String uri = fetchBaseUrl.replace("{buri}", nft.getBuri().replace("ipfs://", ""));

                    final NFTCollectionInfoInternal result = client
                            .get()
                            .uri(uri)
                            .retrieve()
                            .bodyToMono(NFTCollectionInfoInternal.class)
                            .retry(3)
                            .block();

                    if (result != null) {
                        NftCollectionInfo info = nftHelper.convertInfoToDbEntry(result);
                        info.setFkCollection(nft.getId());
                        nftCollectionInfoService.insertNoSub(info);
                    } else {
                        throw new Exception();
                    }
                }
            } catch (Exception e) {
                log.info("collection has no info: " + nft.getTick());
                final NftCollectionInfo info = new NftCollectionInfo();
                info.setFkCollection(nft.getId());
                info.setTick(nft.getTick());
                nftCollectionInfoService.insertNoSub(info);
            }
        });
        log.info("Finished the nft info sync:" + LocalDateTime.now());
    }
}
