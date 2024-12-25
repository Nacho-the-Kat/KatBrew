package com.katbrew.rest.tables;

import com.katbrew.entities.jooq.db.tables.pojos.Whitelist;
import com.katbrew.services.base.ApiResponse;
import com.katbrew.services.tables.WhitelistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.katbrew.rest.base.StaticVariables.API_URL_PREFIX;


@RestController
@RequestMapping(API_URL_PREFIX + "/whitelist")
@RequiredArgsConstructor
public class WhitelistRestController {

    private final WhitelistService whitelistService;

    @GetMapping
    public ApiResponse<List<Whitelist>> getWhitelist() {
        return new ApiResponse<>(whitelistService.findAll());
    }

}
