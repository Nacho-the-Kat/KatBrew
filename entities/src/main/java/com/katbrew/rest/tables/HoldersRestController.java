package com.katbrew.rest.tables;

import com.katbrew.entities.jooq.db.tables.pojos.Holder;
import com.katbrew.rest.base.AbstractRestController;
import com.katbrew.services.base.ApiResponse;
import com.katbrew.services.tables.HolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.katbrew.rest.base.StaticVariables.API_URL_PREFIX;


@RestController
@RequestMapping(API_URL_PREFIX + "/holders")
@RequiredArgsConstructor
public class HoldersRestController extends AbstractRestController<Holder, HolderService> {
    private final HolderService holderService;

    @Override
    public ApiResponse<List<Holder>> findAll() {
        return new ApiResponse<>(500, "Not allowed on holders");
    }

    @GetMapping("/holderCount")
    public ApiResponse<List<HolderService.TickHolder>> getHolderCount() {
        return new ApiResponse<>(holderService.getHolderData());
    }

    @PostMapping("/create")
    public Holder createHolder(@RequestBody Holder holder) {
        return holderService.insert(holder);
    }

    @GetMapping("/topHolders")
    public ApiResponse<List> getTopHolders() {
        return new ApiResponse<>(holderService.getTopHolders());
    }
}
