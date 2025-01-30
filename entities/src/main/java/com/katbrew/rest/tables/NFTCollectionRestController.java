package com.katbrew.rest.tables;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollection;
import com.katbrew.pojos.NFTCollectionEntryInternal;
import com.katbrew.pojos.NFTCollectionInfoInternal;
import com.katbrew.rest.base.AbstractRestController;
import com.katbrew.services.tables.NFTCollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.katbrew.rest.base.StaticVariables.API_URL_PREFIX;

@RestController
@RequestMapping(API_URL_PREFIX + "/nft-collection")
@RequiredArgsConstructor
public class NFTCollectionRestController extends AbstractRestController<NftCollection, NFTCollectionService> {
    private final NFTCollectionService service;

    @GetMapping("/tick")
    public NftCollection getByTick(@RequestParam final String tick) {
        return service.getByTick(tick);
    }

    @GetMapping("/info")
    public NFTCollectionInfoInternal getInfoByTick(@RequestParam final String tick) throws JsonProcessingException {
        return service.getInfoByTick(tick);
    }

    @GetMapping("/entries")
    public List<NFTCollectionEntryInternal> getEntriesByTick(@RequestParam final String tick) {
        return service.getEntriesByTick(tick);
    }
}
