/*
 * This file is generated by jOOQ.
 */
package com.nacho.entities.jooq.db.tables.records;


import com.nacho.entities.jooq.db.tables.Lastupdate;
import com.nacho.entities.jooq.db.tables.interfaces.ILastupdate;

import java.time.LocalDateTime;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LastupdateRecord extends UpdatableRecordImpl<LastupdateRecord> implements ILastupdate {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.LastUpdate.id</code>.
     */
    @Override
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.LastUpdate.id</code>.
     */
    @Override
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.LastUpdate.timestamp</code>.
     */
    @Override
    public void setTimestamp(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.LastUpdate.timestamp</code>.
     */
    @Override
    public LocalDateTime getTimestamp() {
        return (LocalDateTime) get(1);
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
    public void from(ILastupdate from) {
        setId(from.getId());
        setTimestamp(from.getTimestamp());
        resetChangedOnNotNull();
    }

    @Override
    public <E extends ILastupdate> E into(E into) {
        into.from(this);
        return into;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LastupdateRecord
     */
    public LastupdateRecord() {
        super(Lastupdate.LASTUPDATE);
    }

    /**
     * Create a detached, initialised LastupdateRecord
     */
    public LastupdateRecord(Integer id, LocalDateTime timestamp) {
        super(Lastupdate.LASTUPDATE);

        setId(id);
        setTimestamp(timestamp);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised LastupdateRecord
     */
    public LastupdateRecord(com.nacho.entities.jooq.db.tables.pojos.Lastupdate value) {
        super(Lastupdate.LASTUPDATE);

        if (value != null) {
            setId(value.getId());
            setTimestamp(value.getTimestamp());
            resetChangedOnNotNull();
        }
    }
}
