/*
 * This file is generated by jOOQ.
 */
package com.katbrew.entities.jooq.db.tables.daos;


import com.katbrew.entities.jooq.db.tables.NftBalance;
import com.katbrew.entities.jooq.db.tables.records.NftBalanceRecord;

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
public class NftBalanceDao extends DAOImpl<NftBalanceRecord, com.katbrew.entities.jooq.db.tables.pojos.NftBalance, BigInteger> {

    /**
     * Create a new NftBalanceDao without any configuration
     */
    public NftBalanceDao() {
        super(NftBalance.NFT_BALANCE, com.katbrew.entities.jooq.db.tables.pojos.NftBalance.class);
    }

    /**
     * Create a new NftBalanceDao with an attached configuration
     */
    public NftBalanceDao(Configuration configuration) {
        super(NftBalance.NFT_BALANCE, com.katbrew.entities.jooq.db.tables.pojos.NftBalance.class, configuration);
    }

    @Override
    public BigInteger getId(com.katbrew.entities.jooq.db.tables.pojos.NftBalance object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftBalance> fetchRangeOfId(BigInteger lowerInclusive, BigInteger upperInclusive) {
        return fetchRange(NftBalance.NFT_BALANCE.ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftBalance> fetchById(BigInteger... values) {
        return fetch(NftBalance.NFT_BALANCE.ID, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public com.katbrew.entities.jooq.db.tables.pojos.NftBalance fetchOneById(BigInteger value) {
        return fetchOne(NftBalance.NFT_BALANCE.ID, value);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public Optional<com.katbrew.entities.jooq.db.tables.pojos.NftBalance> fetchOptionalById(BigInteger value) {
        return fetchOptional(NftBalance.NFT_BALANCE.ID, value);
    }

    /**
     * Fetch records that have <code>holder_id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftBalance> fetchRangeOfHolderId(BigInteger lowerInclusive, BigInteger upperInclusive) {
        return fetchRange(NftBalance.NFT_BALANCE.HOLDER_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>holder_id IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftBalance> fetchByHolderId(BigInteger... values) {
        return fetch(NftBalance.NFT_BALANCE.HOLDER_ID, values);
    }

    /**
     * Fetch records that have <code>fk_nft_entry BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftBalance> fetchRangeOfFkNftEntry(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(NftBalance.NFT_BALANCE.FK_NFT_ENTRY, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>fk_nft_entry IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.NftBalance> fetchByFkNftEntry(Integer... values) {
        return fetch(NftBalance.NFT_BALANCE.FK_NFT_ENTRY, values);
    }
}
