/*
 * This file is generated by jOOQ.
 */
package com.nacho.entities.jooq.db.tables;


import com.nacho.entities.jooq.db.Keys;
import com.nacho.entities.jooq.db.Public;
import com.nacho.entities.jooq.db.tables.records.TokensRecord;

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
public class Tokens extends TableImpl<TokensRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.Tokens</code>
     */
    public static final Tokens TOKENS = new Tokens();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TokensRecord> getRecordType() {
        return TokensRecord.class;
    }

    /**
     * The column <code>public.Tokens.id</code>.
     */
    public final TableField<TokensRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.Tokens.username</code>.
     */
    public final TableField<TokensRecord, String> USERNAME = createField(DSL.name("username"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>public.Tokens.password</code>.
     */
    public final TableField<TokensRecord, String> PASSWORD = createField(DSL.name("password"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    private Tokens(Name alias, Table<TokensRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Tokens(Name alias, Table<TokensRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>public.Tokens</code> table reference
     */
    public Tokens(String alias) {
        this(DSL.name(alias), TOKENS);
    }

    /**
     * Create an aliased <code>public.Tokens</code> table reference
     */
    public Tokens(Name alias) {
        this(alias, TOKENS);
    }

    /**
     * Create a <code>public.Tokens</code> table reference
     */
    public Tokens() {
        this(DSL.name("Tokens"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public Identity<TokensRecord, Integer> getIdentity() {
        return (Identity<TokensRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<TokensRecord> getPrimaryKey() {
        return Keys.PK_TOKENS;
    }

    @Override
    public Tokens as(String alias) {
        return new Tokens(DSL.name(alias), this);
    }

    @Override
    public Tokens as(Name alias) {
        return new Tokens(alias, this);
    }

    @Override
    public Tokens as(Table<?> alias) {
        return new Tokens(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Tokens rename(String name) {
        return new Tokens(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Tokens rename(Name name) {
        return new Tokens(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Tokens rename(Table<?> name) {
        return new Tokens(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Tokens where(Condition condition) {
        return new Tokens(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Tokens where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Tokens where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Tokens where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Tokens where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Tokens where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Tokens where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Tokens where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Tokens whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Tokens whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
