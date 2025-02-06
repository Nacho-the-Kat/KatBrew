package com.katbrew.services.tables;


import com.katbrew.entities.jooq.db.tables.daos.NftBalanceDao;
import com.katbrew.entities.jooq.db.tables.pojos.NftBalance;
import com.katbrew.services.base.JooqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NFTBalanceService extends JooqService<NftBalance, NftBalanceDao> {

}
