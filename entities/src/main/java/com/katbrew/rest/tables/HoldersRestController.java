package com.katbrew.rest.tables;

import com.katbrew.entities.jooq.db.tables.pojos.Holder;
import com.katbrew.rest.base.AbstractRestController;
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
public class HoldersRestController extends AbstractRestController<Holder, HolderService> {
    private final HolderService holderService;

    @GetMapping("/holderCount")
    public List<HolderService.TickHolder> getHolderCount() {
        return holderService.getHolderData();
    }
}
