/*
 * This file is generated by jOOQ.
 */
package com.katbrew.entities.jooq.db.tables.records;


import com.katbrew.entities.jooq.db.tables.NftTransaction;
import com.katbrew.entities.jooq.db.tables.interfaces.INftTransaction;

import java.math.BigInteger;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class NftTransactionRecord extends UpdatableRecordImpl<NftTransactionRecord> implements INftTransaction {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.nft_transaction.id</code>.
     */
    @Override
    public void setId(BigInteger value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.nft_transaction.id</code>.
     */
    @Override
    public BigInteger getId() {
        return (BigInteger) get(0);
    }

    /**
     * Setter for <code>public.nft_transaction.p</code>.
     */
    @Override
    public void setP(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.nft_transaction.p</code>.
     */
    @Override
    public String getP() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.nft_transaction.op</code>.
     */
    @Override
    public void setOp(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.nft_transaction.op</code>.
     */
    @Override
    public String getOp() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.nft_transaction.deployer</code>.
     */
    @Override
    public void setDeployer(BigInteger value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.nft_transaction.deployer</code>.
     */
    @Override
    public BigInteger getDeployer() {
        return (BigInteger) get(3);
    }

    /**
     * Setter for <code>public.nft_transaction.to_address</code>.
     */
    @Override
    public void setToAddress(BigInteger value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.nft_transaction.to_address</code>.
     */
    @Override
    public BigInteger getToAddress() {
        return (BigInteger) get(4);
    }

    /**
     * Setter for <code>public.nft_transaction.fk_nft_collection</code>.
     */
    @Override
    public void setFkNftCollection(BigInteger value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.nft_transaction.fk_nft_collection</code>.
     */
    @Override
    public BigInteger getFkNftCollection() {
        return (BigInteger) get(5);
    }

    /**
     * Setter for <code>public.nft_transaction.transaction_tick</code>.
     */
    @Override
    public void setTransactionTick(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.nft_transaction.transaction_tick</code>.
     */
    @Override
    public String getTransactionTick() {
        return (String) get(6);
    }

    /**
     * Setter for <code>public.nft_transaction.op_data</code>.
     */
    @Override
    public void setOpData(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.nft_transaction.op_data</code>.
     */
    @Override
    public String getOpData() {
        return (String) get(7);
    }

    /**
     * Setter for <code>public.nft_transaction.op_score</code>.
     */
    @Override
    public void setOpScore(BigInteger value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.nft_transaction.op_score</code>.
     */
    @Override
    public BigInteger getOpScore() {
        return (BigInteger) get(8);
    }

    /**
     * Setter for <code>public.nft_transaction.tx_id_rev</code>.
     */
    @Override
    public void setTxIdRev(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.nft_transaction.tx_id_rev</code>.
     */
    @Override
    public String getTxIdRev() {
        return (String) get(9);
    }

    /**
     * Setter for <code>public.nft_transaction.op_error</code>.
     */
    @Override
    public void setOpError(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.nft_transaction.op_error</code>.
     */
    @Override
    public String getOpError() {
        return (String) get(10);
    }

    /**
     * Setter for <code>public.nft_transaction.mts_add</code>.
     */
    @Override
    public void setMtsAdd(BigInteger value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.nft_transaction.mts_add</code>.
     */
    @Override
    public BigInteger getMtsAdd() {
        return (BigInteger) get(11);
    }

    /**
     * Setter for <code>public.nft_transaction.fee_rev</code>.
     */
    @Override
    public void setFeeRev(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>public.nft_transaction.fee_rev</code>.
     */
    @Override
    public String getFeeRev() {
        return (String) get(12);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<BigInteger> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(INftTransaction from) {
        setId(from.getId());
        setP(from.getP());
        setOp(from.getOp());
        setDeployer(from.getDeployer());
        setToAddress(from.getToAddress());
        setFkNftCollection(from.getFkNftCollection());
        setTransactionTick(from.getTransactionTick());
        setOpData(from.getOpData());
        setOpScore(from.getOpScore());
        setTxIdRev(from.getTxIdRev());
        setOpError(from.getOpError());
        setMtsAdd(from.getMtsAdd());
        setFeeRev(from.getFeeRev());
        resetChangedOnNotNull();
    }

    @Override
    public <E extends INftTransaction> E into(E into) {
        into.from(this);
        return into;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached NftTransactionRecord
     */
    public NftTransactionRecord() {
        super(NftTransaction.NFT_TRANSACTION);
    }

    /**
     * Create a detached, initialised NftTransactionRecord
     */
    public NftTransactionRecord(BigInteger id, String p, String op, BigInteger deployer, BigInteger toAddress, BigInteger fkNftCollection, String transactionTick, String opData, BigInteger opScore, String txIdRev, String opError, BigInteger mtsAdd, String feeRev) {
        super(NftTransaction.NFT_TRANSACTION);

        setId(id);
        setP(p);
        setOp(op);
        setDeployer(deployer);
        setToAddress(toAddress);
        setFkNftCollection(fkNftCollection);
        setTransactionTick(transactionTick);
        setOpData(opData);
        setOpScore(opScore);
        setTxIdRev(txIdRev);
        setOpError(opError);
        setMtsAdd(mtsAdd);
        setFeeRev(feeRev);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised NftTransactionRecord
     */
    public NftTransactionRecord(com.katbrew.entities.jooq.db.tables.pojos.NftTransaction value) {
        super(NftTransaction.NFT_TRANSACTION);

        if (value != null) {
            setId(value.getId());
            setP(value.getP());
            setOp(value.getOp());
            setDeployer(value.getDeployer());
            setToAddress(value.getToAddress());
            setFkNftCollection(value.getFkNftCollection());
            setTransactionTick(value.getTransactionTick());
            setOpData(value.getOpData());
            setOpScore(value.getOpScore());
            setTxIdRev(value.getTxIdRev());
            setOpError(value.getOpError());
            setMtsAdd(value.getMtsAdd());
            setFeeRev(value.getFeeRev());
            resetChangedOnNotNull();
        }
    }
}
