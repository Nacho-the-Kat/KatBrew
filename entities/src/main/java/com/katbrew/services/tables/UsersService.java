package com.katbrew.services.tables;

import com.katbrew.entities.jooq.db.tables.daos.UsersDao;
import com.katbrew.entities.jooq.db.tables.pojos.Users;
import com.katbrew.services.base.JooqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * We need this service only for admin things to login.
 */
@Service
@Slf4j
public class UsersService extends JooqService<Users, UsersDao> {

}
