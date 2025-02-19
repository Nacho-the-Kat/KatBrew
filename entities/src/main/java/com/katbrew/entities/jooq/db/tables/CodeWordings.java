/*
 * This file is generated by jOOQ.
 */
package com.katbrew.entities.jooq.db.tables;


import com.katbrew.entities.jooq.db.Keys;
import com.katbrew.entities.jooq.db.Public;
import com.katbrew.entities.jooq.db.tables.Transaction.TransactionPath;
import com.katbrew.entities.jooq.db.tables.records.CodeWordingsRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
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
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class CodeWordings extends TableImpl<CodeWordingsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.code_wordings</code>
     */
    public static final CodeWordings CODE_WORDINGS = new CodeWordings();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CodeWordingsRecord> getRecordType() {
        return CodeWordingsRecord.class;
    }

    /**
     * The column <code>public.code_wordings.id</code>.
     */
    public final TableField<CodeWordingsRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.code_wordings.identifier</code>.
     */
    public final TableField<CodeWordingsRecord, String> IDENTIFIER = createField(DSL.name("identifier"), SQLDataType.CLOB.nullable(false), this, "");

    private CodeWordings(Name alias, Table<CodeWordingsRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private CodeWordings(Name alias, Table<CodeWordingsRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>public.code_wordings</code> table reference
     */
    public CodeWordings(String alias) {
        this(DSL.name(alias), CODE_WORDINGS);
    }

    /**
     * Create an aliased <code>public.code_wordings</code> table reference
     */
    public CodeWordings(Name alias) {
        this(alias, CODE_WORDINGS);
    }

    /**
     * Create a <code>public.code_wordings</code> table reference
     */
    public CodeWordings() {
        this(DSL.name("code_wordings"), null);
    }

    public <O extends Record> CodeWordings(Table<O> path, ForeignKey<O, CodeWordingsRecord> childPath, InverseForeignKey<O, CodeWordingsRecord> parentPath) {
        super(path, childPath, parentPath, CODE_WORDINGS);
    }

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    public static class CodeWordingsPath extends CodeWordings implements Path<CodeWordingsRecord> {

        private static final long serialVersionUID = 1L;
        public <O extends Record> CodeWordingsPath(Table<O> path, ForeignKey<O, CodeWordingsRecord> childPath, InverseForeignKey<O, CodeWordingsRecord> parentPath) {
            super(path, childPath, parentPath);
        }
        private CodeWordingsPath(Name alias, Table<CodeWordingsRecord> aliased) {
            super(alias, aliased);
        }

        @Override
        public CodeWordingsPath as(String alias) {
            return new CodeWordingsPath(DSL.name(alias), this);
        }

        @Override
        public CodeWordingsPath as(Name alias) {
            return new CodeWordingsPath(alias, this);
        }

        @Override
        public CodeWordingsPath as(Table<?> alias) {
            return new CodeWordingsPath(alias.getQualifiedName(), this);
        }
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public Identity<CodeWordingsRecord, Integer> getIdentity() {
        return (Identity<CodeWordingsRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<CodeWordingsRecord> getPrimaryKey() {
        return Keys.PK_CODE_WORDINGS;
    }

    @Override
    public List<UniqueKey<CodeWordingsRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.CODE_WORDINGS_IDENTIFIER_KEY);
    }

    private transient TransactionPath _fkTransactionOpCodesWordings;

    /**
     * Get the implicit to-many join path to the <code>public.Transaction</code>
     * table, via the <code>fk_transaction_op_codes_wordings</code> key
     */
    public TransactionPath fkTransactionOpCodesWordings() {
        if (_fkTransactionOpCodesWordings == null)
            _fkTransactionOpCodesWordings = new TransactionPath(this, null, Keys.TRANSACTION__FK_TRANSACTION_OP_CODES_WORDINGS.getInverseKey());

        return _fkTransactionOpCodesWordings;
    }

    private transient TransactionPath _fkTransactionOpErrorCodesWordings;

    /**
     * Get the implicit to-many join path to the <code>public.Transaction</code>
     * table, via the <code>fk_transaction_op_error_codes_wordings</code> key
     */
    public TransactionPath fkTransactionOpErrorCodesWordings() {
        if (_fkTransactionOpErrorCodesWordings == null)
            _fkTransactionOpErrorCodesWordings = new TransactionPath(this, null, Keys.TRANSACTION__FK_TRANSACTION_OP_ERROR_CODES_WORDINGS.getInverseKey());

        return _fkTransactionOpErrorCodesWordings;
    }

    private transient TransactionPath _fkTransactionPCodesWordings;

    /**
     * Get the implicit to-many join path to the <code>public.Transaction</code>
     * table, via the <code>fk_transaction_p_codes_wordings</code> key
     */
    public TransactionPath fkTransactionPCodesWordings() {
        if (_fkTransactionPCodesWordings == null)
            _fkTransactionPCodesWordings = new TransactionPath(this, null, Keys.TRANSACTION__FK_TRANSACTION_P_CODES_WORDINGS.getInverseKey());

        return _fkTransactionPCodesWordings;
    }

    @Override
    public CodeWordings as(String alias) {
        return new CodeWordings(DSL.name(alias), this);
    }

    @Override
    public CodeWordings as(Name alias) {
        return new CodeWordings(alias, this);
    }

    @Override
    public CodeWordings as(Table<?> alias) {
        return new CodeWordings(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public CodeWordings rename(String name) {
        return new CodeWordings(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public CodeWordings rename(Name name) {
        return new CodeWordings(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public CodeWordings rename(Table<?> name) {
        return new CodeWordings(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public CodeWordings where(Condition condition) {
        return new CodeWordings(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public CodeWordings where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public CodeWordings where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public CodeWordings where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public CodeWordings where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public CodeWordings where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public CodeWordings where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public CodeWordings where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public CodeWordings whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public CodeWordings whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
