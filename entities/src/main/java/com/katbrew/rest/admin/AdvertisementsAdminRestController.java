package com.katbrew.rest.admin;

import com.katbrew.entities.jooq.db.tables.pojos.Advertisements;
import com.katbrew.services.tables.AdvertisementsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.katbrew.rest.base.StaticVariables.ADMIN_URL_PREFIX;

@RestController
@RequestMapping(ADMIN_URL_PREFIX + "/advertisements")
@RequiredArgsConstructor
public class AdvertisementsAdminRestController extends BaseAdminRestController<Advertisements, AdvertisementsService> {
}
