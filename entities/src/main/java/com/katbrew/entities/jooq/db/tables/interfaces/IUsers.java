/*
 * This file is generated by jOOQ.
 */
package com.katbrew.entities.jooq.db.tables.interfaces;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public interface IUsers extends Serializable {

    /**
     * Setter for <code>public.Users.id</code>.
     */
    public void setId(Integer value);

    /**
     * Getter for <code>public.Users.id</code>.
     */
    public Integer getId();

    /**
     * Setter for <code>public.Users.username</code>.
     */
    public void setUsername(String value);

    /**
     * Getter for <code>public.Users.username</code>.
     */
    public String getUsername();

    /**
     * Setter for <code>public.Users.password</code>.
     */
    public void setPassword(String value);

    /**
     * Getter for <code>public.Users.password</code>.
     */
    public String getPassword();

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    /**
     * Load data from another generated Record/POJO implementing the common
     * interface IUsers
     */
    public void from(IUsers from);

    /**
     * Copy data into another generated Record/POJO implementing the common
     * interface IUsers
     */
    public <E extends IUsers> E into(E into);
}
