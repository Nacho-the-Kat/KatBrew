package com.katbrew.services.tables;

import com.katbrew.entities.jooq.db.tables.daos.AnnouncementsDao;
import com.katbrew.entities.jooq.db.tables.pojos.Announcements;
import com.katbrew.entities.jooq.db.tables.records.AnnouncementsRecord;
import com.katbrew.services.base.JooqService;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementsService extends JooqService<Announcements, AnnouncementsRecord> {

    public AnnouncementsService(DSLContext context) {
        super(new AnnouncementsDao(), context);
    }
}
