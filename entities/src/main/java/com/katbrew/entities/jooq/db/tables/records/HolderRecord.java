/*
 * This file is generated by jOOQ.
 */
package com.katbrew.entities.jooq.db.tables.records;


import com.katbrew.entities.jooq.db.tables.Holder;
import com.katbrew.entities.jooq.db.tables.interfaces.IHolder;

import java.math.BigInteger;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class HolderRecord extends UpdatableRecordImpl<HolderRecord> implements IHolder {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.Holder.id</code>.
     */
    @Override
    public void setId(BigInteger value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.Holder.id</code>.
     */
    @Override
    public BigInteger getId() {
        return (BigInteger) get(0);
    }

    /**
     * Setter for <code>public.Holder.address</code>.
     */
    @Override
    public void setAddress(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.Holder.address</code>.
     */
    @Override
    public String getAddress() {
        return (String) get(1);
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
    public void from(IHolder from) {
        setId(from.getId());
        setAddress(from.getAddress());
        resetChangedOnNotNull();
    }

    @Override
    public <E extends IHolder> E into(E into) {
        into.from(this);
        return into;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached HolderRecord
     */
    public HolderRecord() {
        super(Holder.HOLDER);
    }

    /**
     * Create a detached, initialised HolderRecord
     */
    public HolderRecord(BigInteger id, String address) {
        super(Holder.HOLDER);

        setId(id);
        setAddress(address);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised HolderRecord
     */
    public HolderRecord(com.katbrew.entities.jooq.db.tables.pojos.Holder value) {
        super(Holder.HOLDER);

        if (value != null) {
            setId(value.getId());
            setAddress(value.getAddress());
            resetChangedOnNotNull();
        }
    }
}
