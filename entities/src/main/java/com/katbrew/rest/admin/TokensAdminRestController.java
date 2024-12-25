package com.katbrew.rest.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.services.tables.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.katbrew.rest.base.StaticVariables.ADMIN_URL_PREFIX;

@RestController
@RequestMapping(ADMIN_URL_PREFIX + "/tokens")
@RequiredArgsConstructor
public class TokensAdminRestController {
    private final TokenService tokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @PostMapping
    public List<Token> insertAll(@RequestBody List<Token> token){
        return tokenService.insert(token);
    }
}
