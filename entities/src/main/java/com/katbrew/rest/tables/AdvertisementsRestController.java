package com.katbrew.rest.tables;

import com.katbrew.entities.jooq.db.tables.pojos.Advertisements;
import com.katbrew.rest.base.AbstractRestController;
import com.katbrew.services.tables.AdvertisementsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.katbrew.rest.base.StaticVariables.API_URL_PREFIX;

@RestController
@RequestMapping(API_URL_PREFIX + "/advertisements")
@RequiredArgsConstructor
public class AdvertisementsRestController extends AbstractRestController<Advertisements, AdvertisementsService> {

}
