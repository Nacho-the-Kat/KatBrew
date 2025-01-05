package com.katbrew.rest.admin;

import com.katbrew.entities.jooq.db.tables.pojos.Whitelist;
import com.katbrew.services.tables.WhitelistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.katbrew.rest.base.StaticVariables.ADMIN_URL_PREFIX;

@RestController
@RequestMapping(ADMIN_URL_PREFIX + "/whitelist")
@RequiredArgsConstructor
public class WhitelistAdminRestController extends BaseAdminRestController<Whitelist, WhitelistService> {

    private final WhitelistService whitelistService;

    @PostMapping("/bulkUpload")
    public void bulkUpload(@RequestBody final List<Whitelist> whitelist) {
        //filter out duplicates
        final Map<String, Whitelist> asmap = new HashMap<>();
        whitelist.forEach(single -> asmap.put(single.getAddress(), single));
        whitelistService.batchInsertVoid(asmap.values().stream().toList());
    }
}
