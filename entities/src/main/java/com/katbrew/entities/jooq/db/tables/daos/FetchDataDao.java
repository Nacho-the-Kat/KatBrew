/*
 * This file is generated by jOOQ.
 */
package com.katbrew.entities.jooq.db.tables.daos;


import com.katbrew.entities.jooq.db.tables.FetchData;
import com.katbrew.entities.jooq.db.tables.records.FetchDataRecord;

import java.util.List;
import java.util.Optional;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class FetchDataDao extends DAOImpl<FetchDataRecord, com.katbrew.entities.jooq.db.tables.pojos.FetchData, Integer> {

    /**
     * Create a new FetchDataDao without any configuration
     */
    public FetchDataDao() {
        super(FetchData.FETCH_DATA, com.katbrew.entities.jooq.db.tables.pojos.FetchData.class);
    }

    /**
     * Create a new FetchDataDao with an attached configuration
     */
    public FetchDataDao(Configuration configuration) {
        super(FetchData.FETCH_DATA, com.katbrew.entities.jooq.db.tables.pojos.FetchData.class, configuration);
    }

    @Override
    public Integer getId(com.katbrew.entities.jooq.db.tables.pojos.FetchData object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.FetchData> fetchRangeOfId(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(FetchData.FETCH_DATA.ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.FetchData> fetchById(Integer... values) {
        return fetch(FetchData.FETCH_DATA.ID, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public com.katbrew.entities.jooq.db.tables.pojos.FetchData fetchOneById(Integer value) {
        return fetchOne(FetchData.FETCH_DATA.ID, value);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public Optional<com.katbrew.entities.jooq.db.tables.pojos.FetchData> fetchOptionalById(Integer value) {
        return fetchOptional(FetchData.FETCH_DATA.ID, value);
    }

    /**
     * Fetch records that have <code>identifier BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.FetchData> fetchRangeOfIdentifier(String lowerInclusive, String upperInclusive) {
        return fetchRange(FetchData.FETCH_DATA.IDENTIFIER, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>identifier IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.FetchData> fetchByIdentifier(String... values) {
        return fetch(FetchData.FETCH_DATA.IDENTIFIER, values);
    }

    /**
     * Fetch a unique record that has <code>identifier = value</code>
     */
    public com.katbrew.entities.jooq.db.tables.pojos.FetchData fetchOneByIdentifier(String value) {
        return fetchOne(FetchData.FETCH_DATA.IDENTIFIER, value);
    }

    /**
     * Fetch a unique record that has <code>identifier = value</code>
     */
    public Optional<com.katbrew.entities.jooq.db.tables.pojos.FetchData> fetchOptionalByIdentifier(String value) {
        return fetchOptional(FetchData.FETCH_DATA.IDENTIFIER, value);
    }

    /**
     * Fetch records that have <code>data BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.FetchData> fetchRangeOfData(String lowerInclusive, String upperInclusive) {
        return fetchRange(FetchData.FETCH_DATA.DATA, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>data IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.FetchData> fetchByData(String... values) {
        return fetch(FetchData.FETCH_DATA.DATA, values);
    }
}
