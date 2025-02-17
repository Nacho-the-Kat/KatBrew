package com.katbrew.services.tables;


import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.daos.NftCollectionEntryDao;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollectionEntry;
import com.katbrew.services.base.JooqService;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NFTCollectionEntryService extends JooqService<NftCollectionEntry, NftCollectionEntryDao> {
    private final DSLContext dsl;
    private final com.katbrew.entities.jooq.db.tables.NftCollectionEntry entryTable = Tables.NFT_COLLECTION_ENTRY;

    public List<NftCollectionEntry> getForTick(final BigInteger collectionId, final Integer offset) {
        return dsl.selectFrom(entryTable)
                .where(entryTable.FK_COLLECTION.eq(collectionId))
                .orderBy(entryTable.ID.asc())
                .limit(1000)
                .offset(Objects.requireNonNullElse(offset, 0))
                .fetch()
                .into(NftCollectionEntry.class);
    }

}
