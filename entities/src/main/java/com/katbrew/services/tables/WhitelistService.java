package com.katbrew.services.tables;


import com.katbrew.entities.jooq.db.tables.daos.WhitelistDao;
import com.katbrew.entities.jooq.db.tables.pojos.Whitelist;
import com.katbrew.services.base.JooqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WhitelistService extends JooqService<Whitelist, WhitelistDao> {

}
