/*
 * This file is generated by jOOQ.
 */
package com.katbrew.entities.jooq.db.tables.interfaces;


import java.io.Serializable;
import java.math.BigInteger;

import org.jooq.impl.AutoConverter;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public interface ITopHolder extends Serializable {

    /**
     * Setter for <code>public.Top_Holder.id</code>.
     */
    public void setId(BigInteger value);

    /**
     * Getter for <code>public.Top_Holder.id</code>.
     */
    public BigInteger getId();

    /**
     * Setter for <code>public.Top_Holder.address</code>.
     */
    public void setAddress(String value);

    /**
     * Getter for <code>public.Top_Holder.address</code>.
     */
    public String getAddress();

    /**
     * Setter for <code>public.Top_Holder.token_count</code>.
     */
    public void setTokenCount(Integer value);

    /**
     * Getter for <code>public.Top_Holder.token_count</code>.
     */
    public Integer getTokenCount();

    /**
     * Setter for <code>public.Top_Holder.balances</code>.
     */
    public void setBalances(String value);

    /**
     * Getter for <code>public.Top_Holder.balances</code>.
     */
    public String getBalances();

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    /**
     * Load data from another generated Record/POJO implementing the common
     * interface ITopHolder
     */
    public void from(ITopHolder from);

    /**
     * Copy data into another generated Record/POJO implementing the common
     * interface ITopHolder
     */
    public <E extends ITopHolder> E into(E into);
}
