/*
 * This file is generated by jOOQ.
 */
package com.katbrew.entities.jooq.db.tables.daos;


import com.katbrew.entities.jooq.db.tables.NftCollectionInfo;
import com.katbrew.entities.jooq.db.tables.records.NftCollectionInfoRecord;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.jooq.Configuration;
import org.jooq.impl.AutoConverter;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class NftCollectionInfoDao extends DAOImpl<NftCollectionInfoRecord, com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo, BigInteger> {

    /**
     * Create a new NftCollectionInfoDao without any configuration
     */
    public NftCollectionInfoDao() {
        super(NftCollectionInfo.NFT_COLLECTION_INFO, com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo.class);
    }

    /**
     * Create a new NftCollectionInfoDao with an attached configuration
     */
    public NftCollectionInfoDao(Configuration configuration) {
        super(NftCollectionInfo.NFT_COLLECTION_INFO, com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo.class, configuration);
    }

    @Override
    public BigInteger getId(com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo> fetchRangeOfId(BigInteger lowerInclusive, BigInteger upperInclusive) {
        return fetchRange(NftCollectionInfo.NFT_COLLECTION_INFO.ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo> fetchById(BigInteger... values) {
        return fetch(NftCollectionInfo.NFT_COLLECTION_INFO.ID, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo fetchOneById(BigInteger value) {
        return fetchOne(NftCollectionInfo.NFT_COLLECTION_INFO.ID, value);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public Optional<com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo> fetchOptionalById(BigInteger value) {
        return fetchOptional(NftCollectionInfo.NFT_COLLECTION_INFO.ID, value);
    }

    /**
     * Fetch records that have <code>fk_collection BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo> fetchRangeOfFkCollection(BigInteger lowerInclusive, BigInteger upperInclusive) {
        return fetchRange(NftCollectionInfo.NFT_COLLECTION_INFO.FK_COLLECTION, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>fk_collection IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo> fetchByFkCollection(BigInteger... values) {
        return fetch(NftCollectionInfo.NFT_COLLECTION_INFO.FK_COLLECTION, values);
    }

    /**
     * Fetch a unique record that has <code>fk_collection = value</code>
     */
    public com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo fetchOneByFkCollection(BigInteger value) {
        return fetchOne(NftCollectionInfo.NFT_COLLECTION_INFO.FK_COLLECTION, value);
    }

    /**
     * Fetch a unique record that has <code>fk_collection = value</code>
     */
    public Optional<com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo> fetchOptionalByFkCollection(BigInteger value) {
        return fetchOptional(NftCollectionInfo.NFT_COLLECTION_INFO.FK_COLLECTION, value);
    }

    /**
     * Fetch records that have <code>name BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo> fetchRangeOfName(String lowerInclusive, String upperInclusive) {
        return fetchRange(NftCollectionInfo.NFT_COLLECTION_INFO.NAME, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>name IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo> fetchByName(String... values) {
        return fetch(NftCollectionInfo.NFT_COLLECTION_INFO.NAME, values);
    }

    /**
     * Fetch records that have <code>tick BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo> fetchRangeOfTick(String lowerInclusive, String upperInclusive) {
        return fetchRange(NftCollectionInfo.NFT_COLLECTION_INFO.TICK, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>tick IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo> fetchByTick(String... values) {
        return fetch(NftCollectionInfo.NFT_COLLECTION_INFO.TICK, values);
    }

    /**
     * Fetch records that have <code>description BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo> fetchRangeOfDescription(String lowerInclusive, String upperInclusive) {
        return fetchRange(NftCollectionInfo.NFT_COLLECTION_INFO.DESCRIPTION, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>description IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo> fetchByDescription(String... values) {
        return fetch(NftCollectionInfo.NFT_COLLECTION_INFO.DESCRIPTION, values);
    }

    /**
     * Fetch records that have <code>image BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo> fetchRangeOfImage(String lowerInclusive, String upperInclusive) {
        return fetchRange(NftCollectionInfo.NFT_COLLECTION_INFO.IMAGE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>image IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo> fetchByImage(String... values) {
        return fetch(NftCollectionInfo.NFT_COLLECTION_INFO.IMAGE, values);
    }

    /**
     * Fetch records that have <code>properties BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo> fetchRangeOfProperties(String lowerInclusive, String upperInclusive) {
        return fetchRange(NftCollectionInfo.NFT_COLLECTION_INFO.PROPERTIES, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>properties IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo> fetchByProperties(String... values) {
        return fetch(NftCollectionInfo.NFT_COLLECTION_INFO.PROPERTIES, values);
    }

    /**
     * Fetch records that have <code>extensions BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo> fetchRangeOfExtensions(String lowerInclusive, String upperInclusive) {
        return fetchRange(NftCollectionInfo.NFT_COLLECTION_INFO.EXTENSIONS, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>extensions IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftCollectionInfo> fetchByExtensions(String... values) {
        return fetch(NftCollectionInfo.NFT_COLLECTION_INFO.EXTENSIONS, values);
    }
}
