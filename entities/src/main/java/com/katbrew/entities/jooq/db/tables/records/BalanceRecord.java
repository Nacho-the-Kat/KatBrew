/*
 * This file is generated by jOOQ.
 */
package com.katbrew.entities.jooq.db.tables.records;


import com.katbrew.entities.jooq.db.tables.Balance;
import com.katbrew.entities.jooq.db.tables.interfaces.IBalance;

import java.math.BigInteger;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BalanceRecord extends UpdatableRecordImpl<BalanceRecord> implements IBalance {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.Balance.id</code>.
     */
    @Override
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.Balance.id</code>.
     */
    @Override
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.Balance.holder_id</code>.
     */
    @Override
    public void setHolderId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.Balance.holder_id</code>.
     */
    @Override
    public Integer getHolderId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>public.Balance.balance</code>.
     */
    @Override
    public void setBalance(BigInteger value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.Balance.balance</code>.
     */
    @Override
    public BigInteger getBalance() {
        return (BigInteger) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(IBalance from) {
        setId(from.getId());
        setHolderId(from.getHolderId());
        setBalance(from.getBalance());
        resetChangedOnNotNull();
    }

    @Override
    public <E extends IBalance> E into(E into) {
        into.from(this);
        return into;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BalanceRecord
     */
    public BalanceRecord() {
        super(Balance.BALANCE);
    }

    /**
     * Create a detached, initialised BalanceRecord
     */
    public BalanceRecord(Integer id, Integer holderId, BigInteger balance) {
        super(Balance.BALANCE);

        setId(id);
        setHolderId(holderId);
        setBalance(balance);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised BalanceRecord
     */
    public BalanceRecord(com.katbrew.entities.jooq.db.tables.pojos.Balance value) {
        super(Balance.BALANCE);

        if (value != null) {
            setId(value.getId());
            setHolderId(value.getHolderId());
            setBalance(value.getBalance());
            resetChangedOnNotNull();
        }
    }
}
