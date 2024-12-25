package com.nacho.services.tables;

import com.nacho.entities.jooq.db.tables.daos.AnnouncementsDao;
import com.nacho.entities.jooq.db.tables.pojos.Announcements;
import com.nacho.entities.jooq.db.tables.records.AnnouncementsRecord;
import com.nacho.services.base.AbstractJooqService;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementsService extends AbstractJooqService<Announcements, AnnouncementsRecord> {

    public AnnouncementsService(final DSLContext configuration) {
        super(new AnnouncementsDao(), new AnnouncementsRecord(), Announcements.class, configuration);
    }

}
