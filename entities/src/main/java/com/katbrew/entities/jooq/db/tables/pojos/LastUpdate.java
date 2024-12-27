/*
 * This file is generated by jOOQ.
 */
package com.katbrew.entities.jooq.db.tables.pojos;


import com.katbrew.entities.jooq.db.tables.interfaces.ILastUpdate;

import java.time.LocalDateTime;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LastUpdate implements ILastUpdate {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String identifier;
    private String data;
    private LocalDateTime timestamp;

    public LastUpdate() {}

    public LastUpdate(ILastUpdate value) {
        this.id = value.getId();
        this.identifier = value.getIdentifier();
        this.data = value.getData();
        this.timestamp = value.getTimestamp();
    }

    public LastUpdate(
        Integer id,
        String identifier,
        String data,
        LocalDateTime timestamp
    ) {
        this.id = id;
        this.identifier = identifier;
        this.data = data;
        this.timestamp = timestamp;
    }

    /**
     * Getter for <code>public.Last_Update.id</code>.
     */
    @Override
    public Integer getId() {
        return this.id;
    }

    /**
     * Setter for <code>public.Last_Update.id</code>.
     */
    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Getter for <code>public.Last_Update.identifier</code>.
     */
    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * Setter for <code>public.Last_Update.identifier</code>.
     */
    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Getter for <code>public.Last_Update.data</code>.
     */
    @Override
    public String getData() {
        return this.data;
    }

    /**
     * Setter for <code>public.Last_Update.data</code>.
     */
    @Override
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Getter for <code>public.Last_Update.timestamp</code>.
     */
    @Override
    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    /**
     * Setter for <code>public.Last_Update.timestamp</code>.
     */
    @Override
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final LastUpdate other = (LastUpdate) obj;
        if (this.id == null) {
            if (other.id != null)
                return false;
        }
        else if (!this.id.equals(other.id))
            return false;
        if (this.identifier == null) {
            if (other.identifier != null)
                return false;
        }
        else if (!this.identifier.equals(other.identifier))
            return false;
        if (this.data == null) {
            if (other.data != null)
                return false;
        }
        else if (!this.data.equals(other.data))
            return false;
        if (this.timestamp == null) {
            if (other.timestamp != null)
                return false;
        }
        else if (!this.timestamp.equals(other.timestamp))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.identifier == null) ? 0 : this.identifier.hashCode());
        result = prime * result + ((this.data == null) ? 0 : this.data.hashCode());
        result = prime * result + ((this.timestamp == null) ? 0 : this.timestamp.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("LastUpdate (");

        sb.append(id);
        sb.append(", ").append(identifier);
        sb.append(", ").append(data);
        sb.append(", ").append(timestamp);

        sb.append(")");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(ILastUpdate from) {
        setId(from.getId());
        setIdentifier(from.getIdentifier());
        setData(from.getData());
        setTimestamp(from.getTimestamp());
    }

    @Override
    public <E extends ILastUpdate> E into(E into) {
        into.from(this);
        return into;
    }
}
