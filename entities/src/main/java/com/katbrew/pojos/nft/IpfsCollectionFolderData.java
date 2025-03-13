package com.katbrew.pojos.nft;

import com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class IpfsCollectionFolderData {
    List<String> imageDataNames = new ArrayList<>();
    Boolean hasCollectionFile = false;
    String collectionFileName;
    Boolean isSingleFileCollection = false;
    NftCollectionInfo nftCollectionInfo;
}
