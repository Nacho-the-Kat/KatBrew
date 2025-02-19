package com.katbrew.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollectionEntry;
import com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo;
import com.katbrew.pojos.NFTCollectionEntryInternal;
import com.katbrew.pojos.NFTCollectionInfoInternal;

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
        intern.setId(info.getId());
        intern.setFkCollection(info.getFkCollection());
        intern.setName(info.getName());
        intern.setDescription(info.getDescription());
        intern.setImage(info.getImage());
        intern.setAttributes(mapper.readValue(info.getAttributes(), new TypeReference<>() {
        }));
        intern.setEdition(intern.getEdition());
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
        intern.setId(internal.getId());
        intern.setFkCollection(internal.getFkCollection());
        intern.setName(internal.getName());
        intern.setDescription(internal.getDescription());
        intern.setImage(internal.getImage().replace("ipfs://", ""));
        intern.setAttributes(mapper.writeValueAsString(internal.getAttributes()));
        intern.setEdition(internal.getEdition());
        return intern;
    }
}
