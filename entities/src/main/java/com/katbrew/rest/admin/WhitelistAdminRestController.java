package com.katbrew.rest.admin;

import com.katbrew.entities.jooq.db.tables.pojos.Whitelist;
import com.katbrew.services.tables.WhitelistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.katbrew.rest.base.StaticVariables.ADMIN_URL_PREFIX;

@RestController
@RequestMapping(ADMIN_URL_PREFIX + "/whitelist")
@RequiredArgsConstructor
public class WhitelistAdminRestController extends BaseAdminRestController<Whitelist, WhitelistService> {

    private final WhitelistService whitelistService;

    @PostMapping("/updateAddress")
    public ResponseEntity<String> updateAddress(@RequestParam final String oldAddress, @RequestBody final Map<String, Object> body) {
        return whitelistService.updateAddress(oldAddress, body);
    }

}
