/*
 * This file is generated by jOOQ.
 */
package com.katbrew.entities.jooq.db.tables.daos;


import com.katbrew.entities.jooq.db.tables.Transaction;
import com.katbrew.entities.jooq.db.tables.records.TransactionRecord;

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
public class TransactionDao extends DAOImpl<TransactionRecord, com.katbrew.entities.jooq.db.tables.pojos.Transaction, BigInteger> {

    /**
     * Create a new TransactionDao without any configuration
     */
    public TransactionDao() {
        super(Transaction.TRANSACTION, com.katbrew.entities.jooq.db.tables.pojos.Transaction.class);
    }

    /**
     * Create a new TransactionDao with an attached configuration
     */
    public TransactionDao(Configuration configuration) {
        super(Transaction.TRANSACTION, com.katbrew.entities.jooq.db.tables.pojos.Transaction.class, configuration);
    }

    @Override
    public BigInteger getId(com.katbrew.entities.jooq.db.tables.pojos.Transaction object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchRangeOfId(BigInteger lowerInclusive, BigInteger upperInclusive) {
        return fetchRange(Transaction.TRANSACTION.ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchById(BigInteger... values) {
        return fetch(Transaction.TRANSACTION.ID, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public com.katbrew.entities.jooq.db.tables.pojos.Transaction fetchOneById(BigInteger value) {
        return fetchOne(Transaction.TRANSACTION.ID, value);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public Optional<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchOptionalById(BigInteger value) {
        return fetchOptional(Transaction.TRANSACTION.ID, value);
    }

    /**
     * Fetch records that have <code>fk_token BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchRangeOfFkToken(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(Transaction.TRANSACTION.FK_TOKEN, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>fk_token IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchByFkToken(Integer... values) {
        return fetch(Transaction.TRANSACTION.FK_TOKEN, values);
    }

    /**
     * Fetch records that have <code>hash_rev BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchRangeOfHashRev(String lowerInclusive, String upperInclusive) {
        return fetchRange(Transaction.TRANSACTION.HASH_REV, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>hash_rev IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchByHashRev(String... values) {
        return fetch(Transaction.TRANSACTION.HASH_REV, values);
    }

    /**
     * Fetch a unique record that has <code>hash_rev = value</code>
     */
    public com.katbrew.entities.jooq.db.tables.pojos.Transaction fetchOneByHashRev(String value) {
        return fetchOne(Transaction.TRANSACTION.HASH_REV, value);
    }

    /**
     * Fetch a unique record that has <code>hash_rev = value</code>
     */
    public Optional<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchOptionalByHashRev(String value) {
        return fetchOptional(Transaction.TRANSACTION.HASH_REV, value);
    }

    /**
     * Fetch records that have <code>p BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchRangeOfP(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(Transaction.TRANSACTION.P, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>p IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchByP(Integer... values) {
        return fetch(Transaction.TRANSACTION.P, values);
    }

    /**
     * Fetch records that have <code>op BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchRangeOfOp(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(Transaction.TRANSACTION.OP, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>op IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchByOp(Integer... values) {
        return fetch(Transaction.TRANSACTION.OP, values);
    }

    /**
     * Fetch records that have <code>amt BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchRangeOfAmt(BigInteger lowerInclusive, BigInteger upperInclusive) {
        return fetchRange(Transaction.TRANSACTION.AMT, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>amt IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchByAmt(BigInteger... values) {
        return fetch(Transaction.TRANSACTION.AMT, values);
    }

    /**
     * Fetch records that have <code>from_address BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchRangeOfFromAddress(BigInteger lowerInclusive, BigInteger upperInclusive) {
        return fetchRange(Transaction.TRANSACTION.FROM_ADDRESS, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>from_address IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchByFromAddress(BigInteger... values) {
        return fetch(Transaction.TRANSACTION.FROM_ADDRESS, values);
    }

    /**
     * Fetch records that have <code>to_address BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchRangeOfToAddress(BigInteger lowerInclusive, BigInteger upperInclusive) {
        return fetchRange(Transaction.TRANSACTION.TO_ADDRESS, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>to_address IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchByToAddress(BigInteger... values) {
        return fetch(Transaction.TRANSACTION.TO_ADDRESS, values);
    }

    /**
     * Fetch records that have <code>op_score BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchRangeOfOpScore(BigInteger lowerInclusive, BigInteger upperInclusive) {
        return fetchRange(Transaction.TRANSACTION.OP_SCORE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>op_score IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchByOpScore(BigInteger... values) {
        return fetch(Transaction.TRANSACTION.OP_SCORE, values);
    }

    /**
     * Fetch records that have <code>fee_rev BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchRangeOfFeeRev(String lowerInclusive, String upperInclusive) {
        return fetchRange(Transaction.TRANSACTION.FEE_REV, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>fee_rev IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchByFeeRev(String... values) {
        return fetch(Transaction.TRANSACTION.FEE_REV, values);
    }

    /**
     * Fetch records that have <code>tx_accept BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchRangeOfTxAccept(String lowerInclusive, String upperInclusive) {
        return fetchRange(Transaction.TRANSACTION.TX_ACCEPT, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>tx_accept IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchByTxAccept(String... values) {
        return fetch(Transaction.TRANSACTION.TX_ACCEPT, values);
    }

    /**
     * Fetch records that have <code>op_accept BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchRangeOfOpAccept(String lowerInclusive, String upperInclusive) {
        return fetchRange(Transaction.TRANSACTION.OP_ACCEPT, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>op_accept IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchByOpAccept(String... values) {
        return fetch(Transaction.TRANSACTION.OP_ACCEPT, values);
    }

    /**
     * Fetch records that have <code>op_error BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchRangeOfOpError(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(Transaction.TRANSACTION.OP_ERROR, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>op_error IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchByOpError(Integer... values) {
        return fetch(Transaction.TRANSACTION.OP_ERROR, values);
    }

    /**
     * Fetch records that have <code>checkpoint BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchRangeOfCheckpoint(String lowerInclusive, String upperInclusive) {
        return fetchRange(Transaction.TRANSACTION.CHECKPOINT, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>checkpoint IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchByCheckpoint(String... values) {
        return fetch(Transaction.TRANSACTION.CHECKPOINT, values);
    }

    /**
     * Fetch records that have <code>mts_add BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchRangeOfMtsAdd(BigInteger lowerInclusive, BigInteger upperInclusive) {
        return fetchRange(Transaction.TRANSACTION.MTS_ADD, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mts_add IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchByMtsAdd(BigInteger... values) {
        return fetch(Transaction.TRANSACTION.MTS_ADD, values);
    }

    /**
     * Fetch records that have <code>mts_mod BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchRangeOfMtsMod(BigInteger lowerInclusive, BigInteger upperInclusive) {
        return fetchRange(Transaction.TRANSACTION.MTS_MOD, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mts_mod IN (values)</code>
     */
    public List<com.katbrew.entities.jooq.db.tables.pojos.Transaction> fetchByMtsMod(BigInteger... values) {
        return fetch(Transaction.TRANSACTION.MTS_MOD, values);
    }
}
