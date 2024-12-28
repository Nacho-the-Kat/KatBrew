/*
 * This file is generated by jOOQ.
 */
package com.katbrew.entities.jooq.db.tables.records;


import com.katbrew.entities.jooq.db.tables.Transaction;
import com.katbrew.entities.jooq.db.tables.interfaces.ITransaction;

import java.math.BigInteger;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TransactionRecord extends UpdatableRecordImpl<TransactionRecord> implements ITransaction {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.Transaction.id</code>.
     */
    @Override
    public void setId(BigInteger value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.Transaction.id</code>.
     */
    @Override
    public BigInteger getId() {
        return (BigInteger) get(0);
    }

    /**
     * Setter for <code>public.Transaction.fk_token</code>.
     */
    @Override
    public void setFkToken(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.Transaction.fk_token</code>.
     */
    @Override
    public Integer getFkToken() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>public.Transaction.hash_rev</code>.
     */
    @Override
    public void setHashRev(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.Transaction.hash_rev</code>.
     */
    @Override
    public String getHashRev() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.Transaction.p</code>.
     */
    @Override
    public void setP(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.Transaction.p</code>.
     */
    @Override
    public String getP() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.Transaction.op</code>.
     */
    @Override
    public void setOp(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.Transaction.op</code>.
     */
    @Override
    public String getOp() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.Transaction.amt</code>.
     */
    @Override
    public void setAmt(BigInteger value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.Transaction.amt</code>.
     */
    @Override
    public BigInteger getAmt() {
        return (BigInteger) get(5);
    }

    /**
     * Setter for <code>public.Transaction.from</code>.
     */
    @Override
    public void setFrom(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.Transaction.from</code>.
     */
    @Override
    public String getFrom() {
        return (String) get(6);
    }

    /**
     * Setter for <code>public.Transaction.to</code>.
     */
    @Override
    public void setTo(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.Transaction.to</code>.
     */
    @Override
    public String getTo() {
        return (String) get(7);
    }

    /**
     * Setter for <code>public.Transaction.op_score</code>.
     */
    @Override
    public void setOpScore(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.Transaction.op_score</code>.
     */
    @Override
    public String getOpScore() {
        return (String) get(8);
    }

    /**
     * Setter for <code>public.Transaction.fee_rev</code>.
     */
    @Override
    public void setFeeRev(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.Transaction.fee_rev</code>.
     */
    @Override
    public String getFeeRev() {
        return (String) get(9);
    }

    /**
     * Setter for <code>public.Transaction.tx_accept</code>.
     */
    @Override
    public void setTxAccept(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.Transaction.tx_accept</code>.
     */
    @Override
    public String getTxAccept() {
        return (String) get(10);
    }

    /**
     * Setter for <code>public.Transaction.op_accept</code>.
     */
    @Override
    public void setOpAccept(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.Transaction.op_accept</code>.
     */
    @Override
    public String getOpAccept() {
        return (String) get(11);
    }

    /**
     * Setter for <code>public.Transaction.op_error</code>.
     */
    @Override
    public void setOpError(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>public.Transaction.op_error</code>.
     */
    @Override
    public String getOpError() {
        return (String) get(12);
    }

    /**
     * Setter for <code>public.Transaction.checkpoint</code>.
     */
    @Override
    public void setCheckpoint(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>public.Transaction.checkpoint</code>.
     */
    @Override
    public String getCheckpoint() {
        return (String) get(13);
    }

    /**
     * Setter for <code>public.Transaction.mts_add</code>.
     */
    @Override
    public void setMtsAdd(BigInteger value) {
        set(14, value);
    }

    /**
     * Getter for <code>public.Transaction.mts_add</code>.
     */
    @Override
    public BigInteger getMtsAdd() {
        return (BigInteger) get(14);
    }

    /**
     * Setter for <code>public.Transaction.mts_mod</code>.
     */
    @Override
    public void setMtsMod(BigInteger value) {
        set(15, value);
    }

    /**
     * Getter for <code>public.Transaction.mts_mod</code>.
     */
    @Override
    public BigInteger getMtsMod() {
        return (BigInteger) get(15);
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
    public void from(ITransaction from) {
        setId(from.getId());
        setFkToken(from.getFkToken());
        setHashRev(from.getHashRev());
        setP(from.getP());
        setOp(from.getOp());
        setAmt(from.getAmt());
        setFrom(from.getFrom());
        setTo(from.getTo());
        setOpScore(from.getOpScore());
        setFeeRev(from.getFeeRev());
        setTxAccept(from.getTxAccept());
        setOpAccept(from.getOpAccept());
        setOpError(from.getOpError());
        setCheckpoint(from.getCheckpoint());
        setMtsAdd(from.getMtsAdd());
        setMtsMod(from.getMtsMod());
        resetChangedOnNotNull();
    }

    @Override
    public <E extends ITransaction> E into(E into) {
        into.from(this);
        return into;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TransactionRecord
     */
    public TransactionRecord() {
        super(Transaction.TRANSACTION);
    }

    /**
     * Create a detached, initialised TransactionRecord
     */
    public TransactionRecord(BigInteger id, Integer fkToken, String hashRev, String p, String op, BigInteger amt, String from, String to, String opScore, String feeRev, String txAccept, String opAccept, String opError, String checkpoint, BigInteger mtsAdd, BigInteger mtsMod) {
        super(Transaction.TRANSACTION);

        setId(id);
        setFkToken(fkToken);
        setHashRev(hashRev);
        setP(p);
        setOp(op);
        setAmt(amt);
        setFrom(from);
        setTo(to);
        setOpScore(opScore);
        setFeeRev(feeRev);
        setTxAccept(txAccept);
        setOpAccept(opAccept);
        setOpError(opError);
        setCheckpoint(checkpoint);
        setMtsAdd(mtsAdd);
        setMtsMod(mtsMod);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised TransactionRecord
     */
    public TransactionRecord(com.katbrew.entities.jooq.db.tables.pojos.Transaction value) {
        super(Transaction.TRANSACTION);

        if (value != null) {
            setId(value.getId());
            setFkToken(value.getFkToken());
            setHashRev(value.getHashRev());
            setP(value.getP());
            setOp(value.getOp());
            setAmt(value.getAmt());
            setFrom(value.getFrom());
            setTo(value.getTo());
            setOpScore(value.getOpScore());
            setFeeRev(value.getFeeRev());
            setTxAccept(value.getTxAccept());
            setOpAccept(value.getOpAccept());
            setOpError(value.getOpError());
            setCheckpoint(value.getCheckpoint());
            setMtsAdd(value.getMtsAdd());
            setMtsMod(value.getMtsMod());
            resetChangedOnNotNull();
        }
    }
}
