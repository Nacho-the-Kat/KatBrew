package com.katbrew.pojos.nft;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class IpfsCollectionFolderData {
    List<String> imageDataNames = new ArrayList<>();
    Boolean hasCollectionFile = false;
    String collectionFileName;
    Boolean isSingleFileCollection = false;
}
