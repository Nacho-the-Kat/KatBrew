/*
 * This file is generated by jOOQ.
 */
package com.katbrew.entities.jooq.db.tables.interfaces;


import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public interface IAdvertisements extends Serializable {

    /**
     * Setter for <code>public.Advertisements.id</code>.
     */
    public void setId(Integer value);

    /**
     * Getter for <code>public.Advertisements.id</code>.
     */
    public Integer getId();

    /**
     * Setter for <code>public.Advertisements.link</code>.
     */
    public void setLink(String value);

    /**
     * Getter for <code>public.Advertisements.link</code>.
     */
    public String getLink();

    /**
     * Setter for <code>public.Advertisements.order</code>.
     */
    public void setOrder(Integer value);

    /**
     * Getter for <code>public.Advertisements.order</code>.
     */
    public Integer getOrder();

    /**
     * Setter for <code>public.Advertisements.active</code>.
     */
    public void setActive(Boolean value);

    /**
     * Getter for <code>public.Advertisements.active</code>.
     */
    public Boolean getActive();

    /**
     * Setter for <code>public.Advertisements.show_until</code>.
     */
    public void setShowUntil(LocalDateTime value);

    /**
     * Getter for <code>public.Advertisements.show_until</code>.
     */
    public LocalDateTime getShowUntil();

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    /**
     * Load data from another generated Record/POJO implementing the common
     * interface IAdvertisements
     */
    public void from(IAdvertisements from);

    /**
     * Copy data into another generated Record/POJO implementing the common
     * interface IAdvertisements
     */
    public <E extends IAdvertisements> E into(E into);
}
