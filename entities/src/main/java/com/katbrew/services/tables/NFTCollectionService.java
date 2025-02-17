package com.katbrew.services.tables;


import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.daos.NftCollectionDao;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollection;
import com.katbrew.services.base.JooqService;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NFTCollectionService extends JooqService<NftCollection, NftCollectionDao> {

    private final DSLContext dsl;
    private final com.katbrew.entities.jooq.db.tables.NftCollection collectionTable = Tables.NFT_COLLECTION;

    public List<NftCollection> getForList(final Integer offset) {
        return dsl.selectFrom(collectionTable)
                .orderBy(collectionTable.OP_SCORE_ADD.asc())
                .limit(1000)
                .offset(Objects.requireNonNullElse(offset, 0))
                .fetch()
                .into(NftCollection.class);
    }
}
