/*
 * This file is generated by jOOQ.
 */
package com.nacho.entities.jooq.db.tables.interfaces;


import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public interface IPricedata extends Serializable {

    /**
     * Setter for <code>public.PriceData.id</code>.
     */
    public void setId(Integer value);

    /**
     * Getter for <code>public.PriceData.id</code>.
     */
    public Integer getId();

    /**
     * Setter for <code>public.PriceData.tick</code>.
     */
    public void setTick(String value);

    /**
     * Getter for <code>public.PriceData.tick</code>.
     */
    public String getTick();

    /**
     * Setter for <code>public.PriceData.timestamp</code>.
     */
    public void setTimestamp(LocalDateTime value);

    /**
     * Getter for <code>public.PriceData.timestamp</code>.
     */
    public LocalDateTime getTimestamp();

    /**
     * Setter for <code>public.PriceData.valueKAS</code>.
     */
    public void setValuekas(Double value);

    /**
     * Getter for <code>public.PriceData.valueKAS</code>.
     */
    public Double getValuekas();

    /**
     * Setter for <code>public.PriceData.valueUSD</code>.
     */
    public void setValueusd(Double value);

    /**
     * Getter for <code>public.PriceData.valueUSD</code>.
     */
    public Double getValueusd();

    /**
     * Setter for <code>public.PriceData.change24h</code>.
     */
    public void setChange24h(Double value);

    /**
     * Getter for <code>public.PriceData.change24h</code>.
     */
    public Double getChange24h();

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    /**
     * Load data from another generated Record/POJO implementing the common
     * interface IPricedata
     */
    public void from(IPricedata from);

    /**
     * Copy data into another generated Record/POJO implementing the common
     * interface IPricedata
     */
    public <E extends IPricedata> E into(E into);
}
