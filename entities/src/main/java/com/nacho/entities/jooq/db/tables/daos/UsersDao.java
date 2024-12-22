/*
 * This file is generated by jOOQ.
 */
package com.nacho.entities.jooq.db.tables.daos;


import com.nacho.entities.jooq.db.tables.Users;
import com.nacho.entities.jooq.db.tables.records.UsersRecord;

import java.util.List;
import java.util.Optional;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UsersDao extends DAOImpl<UsersRecord, com.nacho.entities.jooq.db.tables.pojos.Users, Integer> {

    /**
     * Create a new UsersDao without any configuration
     */
    public UsersDao() {
        super(Users.USERS, com.nacho.entities.jooq.db.tables.pojos.Users.class);
    }

    /**
     * Create a new UsersDao with an attached configuration
     */
    public UsersDao(Configuration configuration) {
        super(Users.USERS, com.nacho.entities.jooq.db.tables.pojos.Users.class, configuration);
    }

    @Override
    public Integer getId(com.nacho.entities.jooq.db.tables.pojos.Users object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.nacho.entities.jooq.db.tables.pojos.Users> fetchRangeOfId(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(Users.USERS.ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<com.nacho.entities.jooq.db.tables.pojos.Users> fetchById(Integer... values) {
        return fetch(Users.USERS.ID, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public com.nacho.entities.jooq.db.tables.pojos.Users fetchOneById(Integer value) {
        return fetchOne(Users.USERS.ID, value);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public Optional<com.nacho.entities.jooq.db.tables.pojos.Users> fetchOptionalById(Integer value) {
        return fetchOptional(Users.USERS.ID, value);
    }

    /**
     * Fetch records that have <code>username BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.nacho.entities.jooq.db.tables.pojos.Users> fetchRangeOfUsername(String lowerInclusive, String upperInclusive) {
        return fetchRange(Users.USERS.USERNAME, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>username IN (values)</code>
     */
    public List<com.nacho.entities.jooq.db.tables.pojos.Users> fetchByUsername(String... values) {
        return fetch(Users.USERS.USERNAME, values);
    }

    /**
     * Fetch records that have <code>password BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<com.nacho.entities.jooq.db.tables.pojos.Users> fetchRangeOfPassword(String lowerInclusive, String upperInclusive) {
        return fetchRange(Users.USERS.PASSWORD, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>password IN (values)</code>
     */
    public List<com.nacho.entities.jooq.db.tables.pojos.Users> fetchByPassword(String... values) {
        return fetch(Users.USERS.PASSWORD, values);
    }
}
