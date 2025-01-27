package com.katbrew.rest.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.katbrew.entities.jooq.db.tables.pojos.Token;
import com.katbrew.rest.base.AbstractRestController;
import com.katbrew.services.tables.TokenService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

import static com.katbrew.rest.base.StaticVariables.ADMIN_URL_PREFIX;

@RestController
@RequestMapping(ADMIN_URL_PREFIX + "/token")
@RequiredArgsConstructor
public class TokensAdminRestController extends BaseAdminRestController<Token, TokenService> {
    private final TokenService tokenService;

    @GetMapping("/idMap")
    public Map<String, Integer> getIdMap() {
        return tokenService.findAll().stream().collect(Collectors.toMap(Token::getTick, Token::getId));
    }

    @PostMapping("/updateSocials")
    public void updateSocials(@RequestParam final String tick, @RequestBody final Map<String, String> socials) throws NotFoundException, JsonProcessingException {
        tokenService.updateSocials(tick, socials);
    }
}
