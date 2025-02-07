package com.katbrew.services.tables;


import com.katbrew.entities.jooq.db.tables.daos.NftCollectionEntryDao;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollectionEntry;
import com.katbrew.services.base.JooqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NFTCollectionEntryService extends JooqService<NftCollectionEntry, NftCollectionEntryDao> {

}
