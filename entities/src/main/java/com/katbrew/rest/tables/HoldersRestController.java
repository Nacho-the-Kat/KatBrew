package com.katbrew.rest.tables;

import com.katbrew.services.tables.HolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.katbrew.rest.base.StaticVariables.API_URL_PREFIX;


@RestController
@RequestMapping(API_URL_PREFIX + "/holders")
@RequiredArgsConstructor
public class HoldersRestController {
    private final HolderService holderService;

    @GetMapping
    public List<HolderService.TickHolder> getHolderCount(){
        return holderService.getHolderData();
    }
}
