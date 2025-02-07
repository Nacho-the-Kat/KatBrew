package com.katbrew.services.tables;


import com.katbrew.entities.jooq.db.tables.daos.NftCollectionInfoDao;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo;
import com.katbrew.services.base.JooqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NFTCollectionInfoService extends JooqService<NftCollectionInfo, NftCollectionInfoDao> {

}
