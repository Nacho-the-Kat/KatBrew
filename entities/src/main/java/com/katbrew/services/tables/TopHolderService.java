package com.katbrew.services.tables;

import com.katbrew.entities.jooq.db.tables.daos.TopHolderDao;
import com.katbrew.entities.jooq.db.tables.pojos.TopHolder;
import com.katbrew.services.base.JooqService;
import org.springframework.stereotype.Service;

@Service
public class TopHolderService extends JooqService<TopHolder, TopHolderDao> {

}
