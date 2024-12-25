package com.katbrew.rest.tables;

import com.katbrew.entities.jooq.db.tables.pojos.PriceData;
import com.katbrew.services.base.ApiResponse;
import com.katbrew.services.tables.PriceDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static com.katbrew.rest.base.StaticVariables.API_URL_PREFIX;


@RestController
@RequestMapping(API_URL_PREFIX + "/pricedata")
@RequiredArgsConstructor
public class PriceDataRestController {

    private final PriceDataService priceDataService;

    @GetMapping
    public ApiResponse<List<PriceData>> getTokenPriceData(@RequestParam final String tick, @RequestParam final LocalDateTime start, @RequestParam final LocalDateTime end) {
        return new ApiResponse<>(priceDataService.getTokenPriceData(tick, start, end));
    }

}
