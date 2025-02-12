/*
 * This file is generated by jOOQ.
 */
package com.katbrew.entities.jooq.db.tables.pojos;


import com.katbrew.entities.jooq.db.tables.interfaces.IHolder;

import java.math.BigInteger;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Holder implements IHolder {

    private static final long serialVersionUID = 1L;

    private BigInteger id;
    private String address;

    public Holder() {}

    public Holder(IHolder value) {
        this.id = value.getId();
        this.address = value.getAddress();
    }

    public Holder(
        BigInteger id,
        String address
    ) {
        this.id = id;
        this.address = address;
    }

    /**
     * Getter for <code>public.Holder.id</code>.
     */
    @Override
    public BigInteger getId() {
        return this.id;
    }

    /**
     * Setter for <code>public.Holder.id</code>.
     */
    @Override
    public void setId(BigInteger id) {
        this.id = id;
    }

    /**
     * Getter for <code>public.Holder.address</code>.
     */
    @Override
    public String getAddress() {
        return this.address;
    }

    /**
     * Setter for <code>public.Holder.address</code>.
     */
    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Holder other = (Holder) obj;
        if (this.id == null) {
            if (other.id != null)
                return false;
        }
        else if (!this.id.equals(other.id))
            return false;
        if (this.address == null) {
            if (other.address != null)
                return false;
        }
        else if (!this.address.equals(other.address))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.address == null) ? 0 : this.address.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Holder (");

        sb.append(id);
        sb.append(", ").append(address);

        sb.append(")");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(IHolder from) {
        setId(from.getId());
        setAddress(from.getAddress());
    }

    @Override
    public <E extends IHolder> E into(E into) {
        into.from(this);
        return into;
    }
}
