package com.katbrew.services.tables;

import com.katbrew.entities.jooq.db.tables.daos.AnnouncementsDao;
import com.katbrew.entities.jooq.db.tables.pojos.Announcements;
import com.katbrew.services.base.JooqService;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementsService extends JooqService<Announcements, AnnouncementsDao> {

}
