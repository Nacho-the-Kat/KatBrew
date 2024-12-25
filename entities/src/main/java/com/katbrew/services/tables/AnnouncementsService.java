package com.katbrew.services.tables;

import com.katbrew.entities.jooq.db.tables.daos.AnnouncementsDao;
import com.katbrew.entities.jooq.db.tables.pojos.Announcements;
import com.katbrew.entities.jooq.db.tables.records.AnnouncementsRecord;
import com.katbrew.services.base.AbstractJooqService;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementsService extends AbstractJooqService<Announcements, AnnouncementsRecord> {

    public AnnouncementsService(final DSLContext configuration) {
        super(new AnnouncementsDao(), new AnnouncementsRecord(), Announcements.class, configuration);
    }

}
