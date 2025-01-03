/*
 * This file is generated by jOOQ.
 */
package com.katbrew.entities.jooq.db.tables;


import com.katbrew.entities.jooq.db.Keys;
import com.katbrew.entities.jooq.db.Public;
import com.katbrew.entities.jooq.db.tables.records.AdvertisementsRecord;

import java.time.LocalDateTime;
import java.util.Collection;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.PlainSQL;
import org.jooq.QueryPart;
import org.jooq.SQL;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.Stringly;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Advertisements extends TableImpl<AdvertisementsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.Advertisements</code>
     */
    public static final Advertisements ADVERTISEMENTS = new Advertisements();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AdvertisementsRecord> getRecordType() {
        return AdvertisementsRecord.class;
    }

    /**
     * The column <code>public.Advertisements.id</code>.
     */
    public final TableField<AdvertisementsRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.Advertisements.link</code>.
     */
    public final TableField<AdvertisementsRecord, String> LINK = createField(DSL.name("link"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.Advertisements.order</code>.
     */
    public final TableField<AdvertisementsRecord, Integer> ORDER = createField(DSL.name("order"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>public.Advertisements.active</code>.
     */
    public final TableField<AdvertisementsRecord, Boolean> ACTIVE = createField(DSL.name("active"), SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>public.Advertisements.show_until</code>.
     */
    public final TableField<AdvertisementsRecord, LocalDateTime> SHOW_UNTIL = createField(DSL.name("show_until"), SQLDataType.LOCALDATETIME(6), this, "");

    private Advertisements(Name alias, Table<AdvertisementsRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Advertisements(Name alias, Table<AdvertisementsRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>public.Advertisements</code> table reference
     */
    public Advertisements(String alias) {
        this(DSL.name(alias), ADVERTISEMENTS);
    }

    /**
     * Create an aliased <code>public.Advertisements</code> table reference
     */
    public Advertisements(Name alias) {
        this(alias, ADVERTISEMENTS);
    }

    /**
     * Create a <code>public.Advertisements</code> table reference
     */
    public Advertisements() {
        this(DSL.name("Advertisements"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public Identity<AdvertisementsRecord, Integer> getIdentity() {
        return (Identity<AdvertisementsRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<AdvertisementsRecord> getPrimaryKey() {
        return Keys.PK_ADVERTISEMENTS;
    }

    @Override
    public Advertisements as(String alias) {
        return new Advertisements(DSL.name(alias), this);
    }

    @Override
    public Advertisements as(Name alias) {
        return new Advertisements(alias, this);
    }

    @Override
    public Advertisements as(Table<?> alias) {
        return new Advertisements(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Advertisements rename(String name) {
        return new Advertisements(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Advertisements rename(Name name) {
        return new Advertisements(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Advertisements rename(Table<?> name) {
        return new Advertisements(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Advertisements where(Condition condition) {
        return new Advertisements(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Advertisements where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Advertisements where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Advertisements where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Advertisements where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Advertisements where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Advertisements where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Advertisements where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Advertisements whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Advertisements whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
