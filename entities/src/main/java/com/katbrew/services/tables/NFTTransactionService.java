package com.katbrew.services.tables;


import com.katbrew.entities.jooq.db.tables.daos.NftTransactionDao;
import com.katbrew.entities.jooq.db.tables.pojos.NftTransaction;
import com.katbrew.services.base.JooqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NFTTransactionService extends JooqService<NftTransaction, NftTransactionDao> {

}
