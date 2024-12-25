package com.nacho.rest.tables;

//import com.nacho.entities.jooq.db.tables.pojos.Pricedata;

import com.nacho.entities.jooq.db.tables.pojos.Token;
import com.nacho.entities.jooq.db.tables.records.TokenRecord;
import com.nacho.services.base.ApiResponse;
import com.nacho.services.tables.TokensService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.nacho.rest.base.StaticVariables.API_URL_PREFIX;


@RestController
@RequestMapping(API_URL_PREFIX + "/token")
@RequiredArgsConstructor
public class TokensRestController {

//    private final PriceDataService priceDataService;
    private final TokensService tokensService;

//    @GetMapping("/TokenPriceData")
//    public ApiResponse<List<Pricedata>> getTokenPriceData(
//            @RequestParam final String tick,
//            @RequestParam final LocalDateTime start,
//            @RequestParam final LocalDateTime end
//    ) {
//        return new ApiResponse<>(priceDataService.getTokenPriceData(tick, start, end));
//    }

    @GetMapping("/tokens")
    public ApiResponse<List<Token>> getTokens(@RequestParam Integer limit) {
        return new ApiResponse<>(tokensService.getTokens());
    }

    @GetMapping("/tickers")
    public ApiResponse<List<String>> getTickers() {
        return new ApiResponse<>(tokensService.getTickers());
    }

    @GetMapping("/tokenlist")
    public ApiResponse<List<Token>> getTokenlist(
            @RequestParam (defaultValue="100") final Integer limit,
            @RequestParam (defaultValue = "0", required = false) final String cursor,
            @RequestParam (defaultValue = "holderTotal") final String sortBy,
            @RequestParam (defaultValue = "desc") final String sortOrder
    ) {
        return new ApiResponse<>(tokensService.getTokenList(limit, cursor, sortBy, sortOrder));
    }

}
