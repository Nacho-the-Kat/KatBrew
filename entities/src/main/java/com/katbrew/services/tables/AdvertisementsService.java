package com.katbrew.services.tables;

import com.katbrew.entities.jooq.db.tables.daos.AdvertisementsDao;
import com.katbrew.entities.jooq.db.tables.pojos.Advertisements;
import com.katbrew.services.base.JooqService;
import org.springframework.stereotype.Service;

@Service
public class AdvertisementsService extends JooqService<Advertisements, AdvertisementsDao> {

}
