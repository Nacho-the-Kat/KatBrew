package com.katbrew.rest.admin;

import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.services.tables.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.katbrew.rest.base.StaticVariables.ADMIN_URL_PREFIX;

@RestController
@RequestMapping(ADMIN_URL_PREFIX + "/tokens")
@RequiredArgsConstructor
public class TokensAdminRestController {
    private final TokenService tokenService;

    @PostMapping
    public List<Token> insertAll(@RequestBody List<Token> token) {
        return tokenService.insert(token);
    }

    @GetMapping("/idMap")
    public Map<String, Integer> getIdMap() {
        return tokenService.findAll().stream().collect(Collectors.toMap(Token::getTick, Token::getId));
    }
}
