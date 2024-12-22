package com.nacho.services.tables;

import com.nacho.entities.jooq.db.tables.daos.AnnouncementsDao;
import com.nacho.entities.jooq.db.tables.pojos.Announcements;
import com.nacho.entities.jooq.db.tables.records.AnnouncementsRecord;
import com.nacho.services.base.BaseEntityService;
import org.jooq.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementsService extends BaseEntityService<Announcements, AnnouncementsRecord> {

    public AnnouncementsService(final Configuration configuration) {
        super(new AnnouncementsDao(), new AnnouncementsRecord(), configuration);
    }

}
