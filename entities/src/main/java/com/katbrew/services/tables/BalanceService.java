package com.katbrew.services.tables;

import com.katbrew.entities.jooq.db.tables.daos.BalanceDao;
import com.katbrew.entities.jooq.db.tables.pojos.Balance;
import com.katbrew.services.base.JooqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceService extends JooqService<Balance, BalanceDao> {

}
