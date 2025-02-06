package com.katbrew.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollectionEntry;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo;
import com.katbrew.entities.jooq.db.tables.pojos.NftTransaction;
import com.katbrew.pojos.NFTCollectionEntryInternal;
import com.katbrew.pojos.NFTCollectionInfoInternal;
import com.katbrew.pojos.NftTransactionExternal;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.util.internal.SerializationUtil;
import org.springframework.stereotype.Service;

@Service
public class NftHelper {
    private final ObjectMapper mapper = KatBrewObjectMapper.createObjectMapper();

    public NFTCollectionInfoInternal parse(final NftCollectionInfo info) throws JsonProcessingException {
        final NFTCollectionInfoInternal intern = new NFTCollectionInfoInternal();
        intern.setName(info.getName());
        intern.setDescription(info.getDescription());
        intern.setImage(info.getImage());
        intern.setExtensions(mapper.readValue(info.getExtensions(), new TypeReference<>() {
        }));
        intern.setProperties(mapper.readValue(info.getProperties(), new TypeReference<>() {
        }));
        return intern;
    }

    public NFTCollectionEntryInternal parseEntry(final NftCollectionEntry info) throws JsonProcessingException {
        final NFTCollectionEntryInternal intern = new NFTCollectionEntryInternal();
        intern.setName(info.getName());
        intern.setDescription(info.getDescription());
        intern.setImage(info.getImage());
        intern.setAttributes(mapper.readValue(info.getAttributes(), new TypeReference<>() {
        }));
        intern.setEdition(intern.getEdition());
        return intern;
    }

    public NftTransactionExternal parseTransaction(final NftTransaction info) throws JsonProcessingException {
        final NftTransaction internal = SerializationUtils.clone(info);
        final String opData = new String(internal.getOpData());
        internal.setOpData(null);
        final NftTransactionExternal intern = mapper.convertValue(internal, NftTransactionExternal.class);
        intern.setOpData(mapper.readValue(opData, NftTransactionExternal.OpData.class));

        return intern;
    }

    public NftCollectionInfo convertInfoToDbEntry(final NFTCollectionInfoInternal internal) throws JsonProcessingException {
        final NftCollectionInfo intern = new NftCollectionInfo();
        intern.setName(internal.getName());
        intern.setDescription(internal.getDescription());
        intern.setImage(internal.getImage());
        intern.setExtensions(mapper.writeValueAsString(internal.getExtensions()));
        intern.setProperties(mapper.writeValueAsString(internal.getProperties()));
        return intern;
    }

    public NftCollectionEntry convertEntryToDbEntry(final NFTCollectionEntryInternal internal) throws JsonProcessingException {
        final NftCollectionEntry intern = new NftCollectionEntry();
        intern.setName(internal.getName());
        intern.setDescription(internal.getDescription());
        intern.setImage(internal.getImage());
        intern.setAttributes(mapper.writeValueAsString(internal.getAttributes()));
        intern.setEdition(internal.getEdition());
        return intern;
    }

    public NftTransaction convertTransactionToDbEntry(final NftTransactionExternal internal) throws JsonProcessingException {
        final NftTransactionExternal internalCopy = SerializationUtils.clone(internal);
        final NftTransactionExternal.OpData opData = SerializationUtils.clone(internalCopy.getOpData());
        internalCopy.setOpData(null);
        final NftTransaction intern = mapper.convertValue(internalCopy, NftTransaction.class);
        intern.setOpData(mapper.writeValueAsString(opData));
        return intern;
    }
}
