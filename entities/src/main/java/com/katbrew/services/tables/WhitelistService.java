package com.katbrew.services.tables;


import com.katbrew.entities.jooq.db.tables.daos.WhitelistDao;
import com.katbrew.entities.jooq.db.tables.pojos.Whitelist;
import com.katbrew.entities.jooq.db.tables.records.WhitelistRecord;
import com.katbrew.services.base.JooqService;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

@Service
public class WhitelistService extends JooqService<Whitelist, WhitelistRecord> {

    public WhitelistService(DSLContext dsl) {
        super(new WhitelistDao(), dsl);
    }
}
