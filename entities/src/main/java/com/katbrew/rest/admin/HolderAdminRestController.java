package com.katbrew.rest.admin;

import com.katbrew.entities.jooq.db.tables.pojos.Holder;
import com.katbrew.entities.jooq.db.tables.pojos.Whitelist;
import com.katbrew.services.tables.HolderService;
import com.katbrew.services.tables.WhitelistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.katbrew.rest.base.StaticVariables.ADMIN_URL_PREFIX;

@RestController
@RequestMapping(ADMIN_URL_PREFIX + "/holder")
@RequiredArgsConstructor
public class HolderAdminRestController extends BaseAdminRestController<Whitelist, WhitelistService> {

    private final HolderService holderService;

    @PostMapping("/bulkUpload")
    public void bulkUpload(@RequestBody final List<Holder> holders) {
        //filter out duplicates
        holderService.batchInsertVoid(new HashSet<>(holders));
    }

    @GetMapping("/idMap")
    public Map<String, BigInteger> getIdMap() {
        //filter out duplicates
        return holderService.findAll().stream().collect(Collectors.toMap(Holder::getAddress, Holder::getId));
    }
}
