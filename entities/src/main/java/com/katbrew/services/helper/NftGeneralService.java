package com.katbrew.services.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollection;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollectionEntry;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo;
import com.katbrew.helper.NftHelper;
import com.katbrew.pojos.NFTCollectionEntryInternal;
import com.katbrew.pojos.NFTCollectionInfoInternal;
import com.katbrew.services.tables.NFTCollectionEntryService;
import com.katbrew.services.tables.NFTCollectionInfoService;
import com.katbrew.services.tables.NFTCollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NftGeneralService {

    private final NFTCollectionService nftCollectionService;
    private final NFTCollectionInfoService nftCollectionInfoService;
    private final NFTCollectionEntryService nftCollectionEntryService;
    private final NftHelper nftHelper = new NftHelper();
    ;

    public NftCollection getByTick(final String tick) {
        final List<NftCollection> coll = nftCollectionService.findBy(List.of(Tables.NFT_COLLECTION.TICK.eq(tick)));
        if (coll.isEmpty()) {
            return null;
        }
        return coll.get(0);
    }

    public NFTCollectionInfoInternal getInfoByTick(final String tick) throws JsonProcessingException {
        final NftCollection collection = getByTick(tick);
        if (collection == null) {
            return null;
        }
        final List<NftCollectionInfo> coll = nftCollectionInfoService.findBy(List.of(Tables.NFT_COLLECTION_INFO.FK_COLLECTION.eq(collection.getId())));
        if (coll.isEmpty()) {
            return null;
        }
        return nftHelper.parse(coll.get(0));
    }

    public List<NFTCollectionEntryInternal> getEntriesByTick(final String tick, final Integer offset) {
        final NftCollection collection = getByTick(tick);
        if (collection == null) {
            return null;
        }
        final List<NftCollectionEntry> coll = nftCollectionEntryService.getForTick(collection.getId(), offset);
        if (coll.isEmpty()) {
            return null;
        }
        return coll.stream().map(single -> {
            try {
                return nftHelper.parseEntry(single);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    public List<NftCollection> getCollectionList(final Integer offset) {
        return nftCollectionService.getForList(offset);
    }
}
