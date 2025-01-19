package com.katbrew.rest.tables;

import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.pojos.TokenHolder;
import com.katbrew.rest.base.AbstractRestController;
import com.katbrew.services.base.ApiResponse;
import com.katbrew.services.tables.TokenService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.katbrew.rest.base.StaticVariables.API_URL_PREFIX;


@RestController
@RequestMapping(API_URL_PREFIX + "/token")
@RequiredArgsConstructor
public class TokensRestController extends AbstractRestController<Token, TokenService> {

    private final TokenService tokenService;

    @GetMapping("/tickers")
    public ApiResponse<List<String>> getTickers() {
        return new ApiResponse<>(tokenService.getTickers());
    }

    @GetMapping("/tokenlist")
    public ApiResponse<List<Token>> getTokenlist(
            @RequestParam(defaultValue = "holderTotal") final String sortBy,
            @RequestParam(defaultValue = "desc") final String sortOrder
    ) {
        return new ApiResponse<>(tokenService.getTokenList(sortBy, sortOrder));
    }

    @GetMapping("/detail/{tick}")
    public ApiResponse<Token> getFullToken(
            @PathVariable final String tick
    ) throws NotFoundException {
        final Token token = tokenService.findByTick(tick);
        if (token == null){
            throw new NotFoundException("token not found");
        }
        return new ApiResponse<>(token);
    }
    @GetMapping("/holder/{tick}")
    public ApiResponse<List<TokenHolder>> getTokenHolder(
            @PathVariable final String tick
    ) throws NotFoundException {
        List<TokenHolder> token = tokenService.getHolder(tick);

        return new ApiResponse<>(token);
    }

}
