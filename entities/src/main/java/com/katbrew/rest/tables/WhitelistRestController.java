package com.katbrew.rest.tables;

import com.katbrew.entities.jooq.db.tables.pojos.Whitelist;
import com.katbrew.rest.base.AbstractRestController;
import com.katbrew.services.tables.WhitelistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.katbrew.rest.base.StaticVariables.API_URL_PREFIX;

@RestController
@RequestMapping(API_URL_PREFIX + "/whitelist")
@RequiredArgsConstructor
public class WhitelistRestController extends AbstractRestController<Whitelist, WhitelistService> {

}
