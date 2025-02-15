package com.katbrew.workflows.tasks.nfts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.katbrew.entities.jooq.db.tables.pojos.LastUpdate;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollection;
import com.katbrew.helper.KatBrewHelper;
import com.katbrew.services.tables.LastUpdateService;
import com.katbrew.services.tables.NFTCollectionService;
import com.katbrew.workflows.helper.ParsingResponse;
import com.katbrew.workflows.helper.ParsingResponsePagedNFT;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class FetchNFTS implements JavaDelegate {

    @Value("${data.fetchNFT.list.baseUrl}")
    private String fetchBaseUrl;
    private final LastUpdateService lastUpdateService;
    private final NFTCollectionService nftCollectionService;

    private static String getCursor(ParsingResponsePagedNFT<List<NftCollection>> entity) {
        return entity.getNext();
    }

    @Override
    public void execute(DelegateExecution execution) throws JsonProcessingException {
        log.info("Starting the nft sync:" + LocalDateTime.now());

        final Map<String, BigInteger> nftMap = nftCollectionService.findAll().stream().collect(Collectors.toMap(NftCollection::getTick, NftCollection::getId));

        LastUpdate lastCursor = lastUpdateService.findByIdentifier("fetchNFTLastCursor");

        final ParameterizedTypeReference<ParsingResponsePagedNFT<List<NftCollection>>> reference = new ParameterizedTypeReference<>() {
        };

        final KatBrewHelper<ParsingResponsePagedNFT<List<NftCollection>>, NftCollection> helper = new KatBrewHelper<>();

        try {

            log.info("Start fetching the Transactions, last cursor: " + (lastCursor != null ? lastCursor.getData() : "not existing"));

            final List<NftCollection> result = helper.fetchPaginatedWithoutSave(
                    fetchBaseUrl,
                    lastCursor != null ? lastCursor.getData() : null,
                    false,
                    false,
                    "?offset=",
                    reference,
                    FetchNFTS::getCursor,
                    ParsingResponse::getResult
            );

            if (result != null) {
                final List<NftCollection> toUpdate = new ArrayList<>();
                final List<NftCollection> toInsert = new ArrayList<>();


                result.forEach(single -> {
                    single.setBuri(single.getBuri().replace("ipfs://", ""));
                    final BigInteger id = nftMap.get(single.getTick());
                    if (id != null) {
                        single.setId(id);
                        toUpdate.add(single);
                    } else {
                        toInsert.add(single);
                    }
                });

                nftCollectionService.batchUpdate(toUpdate);
                final List<NftCollection> newCollection = nftCollectionService.insert(toInsert);

                execution.setVariable("newCollectionIds", newCollection.stream().map(NftCollection::getId).toList());

                if (lastCursor == null) {
                    lastCursor = new LastUpdate();
                    lastCursor.setData(null);
                    lastCursor.setIdentifier("fetchNFTLastCursor");
                    lastUpdateService.insert(lastCursor);
                } else {
                    lastCursor.setData(result.get(result.size() - 1).getOpScoreAdd().toString());
                    lastUpdateService.update(lastCursor);
                }

            } else {
                log.error("no NFTs were loaded");
            }

        } catch (Exception e) {
            log.info(e.getMessage());
        }
        log.info("Finished the nft sync:" + LocalDateTime.now());
    }

}
