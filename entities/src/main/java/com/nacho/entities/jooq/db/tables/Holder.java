/*
 * This file is generated by jOOQ.
 */
package com.nacho.entities.jooq.db.tables;


import com.nacho.entities.jooq.db.Indexes;
import com.nacho.entities.jooq.db.Keys;
import com.nacho.entities.jooq.db.Public;
import com.nacho.entities.jooq.db.tables.Balance.BalancePath;
import com.nacho.entities.jooq.db.tables.records.HolderRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.InverseForeignKey;
import org.jooq.Name;
import org.jooq.Path;
import org.jooq.PlainSQL;
import org.jooq.QueryPart;
import org.jooq.Record;
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
public class Holder extends TableImpl<HolderRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.Holder</code>
     */
    public static final Holder HOLDER = new Holder();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<HolderRecord> getRecordType() {
        return HolderRecord.class;
    }

    /**
     * The column <code>public.Holder.id</code>.
     */
    public final TableField<HolderRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.field(DSL.raw("nextval('\"Holder_id_seq\"'::regclass)"), SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>public.Holder.address</code>.
     */
    public final TableField<HolderRecord, String> ADDRESS = createField(DSL.name("address"), SQLDataType.CLOB.nullable(false), this, "");

    private Holder(Name alias, Table<HolderRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Holder(Name alias, Table<HolderRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>public.Holder</code> table reference
     */
    public Holder(String alias) {
        this(DSL.name(alias), HOLDER);
    }

    /**
     * Create an aliased <code>public.Holder</code> table reference
     */
    public Holder(Name alias) {
        this(alias, HOLDER);
    }

    /**
     * Create a <code>public.Holder</code> table reference
     */
    public Holder() {
        this(DSL.name("Holder"), null);
    }

    public <O extends Record> Holder(Table<O> path, ForeignKey<O, HolderRecord> childPath, InverseForeignKey<O, HolderRecord> parentPath) {
        super(path, childPath, parentPath, HOLDER);
    }

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    public static class HolderPath extends Holder implements Path<HolderRecord> {

        private static final long serialVersionUID = 1L;
        public <O extends Record> HolderPath(Table<O> path, ForeignKey<O, HolderRecord> childPath, InverseForeignKey<O, HolderRecord> parentPath) {
            super(path, childPath, parentPath);
        }
        private HolderPath(Name alias, Table<HolderRecord> aliased) {
            super(alias, aliased);
        }

        @Override
        public HolderPath as(String alias) {
            return new HolderPath(DSL.name(alias), this);
        }

        @Override
        public HolderPath as(Name alias) {
            return new HolderPath(alias, this);
        }

        @Override
        public HolderPath as(Table<?> alias) {
            return new HolderPath(alias.getQualifiedName(), this);
        }
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.HOLDER_ADDRESS_KEY);
    }

    @Override
    public UniqueKey<HolderRecord> getPrimaryKey() {
        return Keys.HOLDER_PKEY;
    }

    private transient BalancePath _balance;

    /**
     * Get the implicit to-many join path to the <code>public.Balance</code>
     * table
     */
    public BalancePath balance() {
        if (_balance == null)
            _balance = new BalancePath(this, null, Keys.BALANCE__BALANCE_HOLDERID_FKEY.getInverseKey());

        return _balance;
    }

    @Override
    public Holder as(String alias) {
        return new Holder(DSL.name(alias), this);
    }

    @Override
    public Holder as(Name alias) {
        return new Holder(alias, this);
    }

    @Override
    public Holder as(Table<?> alias) {
        return new Holder(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Holder rename(String name) {
        return new Holder(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Holder rename(Name name) {
        return new Holder(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Holder rename(Table<?> name) {
        return new Holder(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Holder where(Condition condition) {
        return new Holder(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Holder where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Holder where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Holder where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Holder where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Holder where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Holder where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Holder where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Holder whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Holder whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
