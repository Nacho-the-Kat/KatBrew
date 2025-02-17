package com.katbrew.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollection;
import com.katbrew.pojos.NFTCollectionEntryInternal;
import com.katbrew.pojos.NFTCollectionInfoInternal;
import com.katbrew.services.base.ApiResponse;
import com.katbrew.services.helper.NftGeneralService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.katbrew.rest.base.StaticVariables.API_URL_PREFIX;

@RestController
@RequestMapping(API_URL_PREFIX + "/nfts")
@RequiredArgsConstructor
public class NftGeneralRestController {
    private final NftGeneralService service;

    @GetMapping("/list")
    public ApiResponse<List<NftCollection>> getByTick(@RequestParam(required = false) final Integer offset) {
        return new ApiResponse<>(service.getCollectionList(offset));
    }

    @GetMapping("/tick")
    public ApiResponse<NftCollection> getByTick(@RequestParam final String tick) {
        return new ApiResponse<>(service.getByTick(tick));
    }

    @GetMapping("/info")
    public ApiResponse<NFTCollectionInfoInternal> getInfoByTick(@RequestParam final String tick) throws JsonProcessingException {
        return new ApiResponse<>(service.getInfoByTick(tick));
    }

    @GetMapping("/entries")
    public ApiResponse<List<NFTCollectionEntryInternal>> getEntriesByTick(@RequestParam final String tick, @RequestParam(required = false) final Integer offset) {
        return new ApiResponse<>(service.getEntriesByTick(tick, offset));
    }
}
