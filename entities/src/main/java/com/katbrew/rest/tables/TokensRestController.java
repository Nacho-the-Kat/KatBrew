package com.katbrew.rest.tables;

//import com.katbrew.entities.jooq.db.tables.pojos.Pricedata;

import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.rest.base.AbstractRestController;
import com.katbrew.services.base.ApiResponse;
import com.katbrew.services.tables.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.katbrew.rest.base.StaticVariables.API_URL_PREFIX;


@RestController
@RequestMapping(API_URL_PREFIX + "/token")
@RequiredArgsConstructor
public class TokensRestController extends AbstractRestController<Token, TokenService> {

    private final TokenService tokenService;

    @GetMapping("/tokens")
    public ApiResponse<List<Token>> getTokens(@RequestParam Integer limit) {
        return new ApiResponse<>(tokenService.getTokens());
    }

    @GetMapping("/tickers")
    public ApiResponse<List<String>> getTickers() {
        return new ApiResponse<>(tokenService.getTickers());
    }

    @GetMapping("/tokenlist")
    public ApiResponse<List<Token>> getTokenlist(
            @RequestParam(defaultValue = "100") final Integer limit,
            @RequestParam(defaultValue = "0", required = false) final Integer cursor,
            @RequestParam(defaultValue = "holderTotal") final String sortBy,
            @RequestParam(defaultValue = "desc") final String sortOrder
    ) {
        return new ApiResponse<>(tokenService.getTokenList(limit, cursor, sortBy, sortOrder));
    }

}
