/*
 * This file is generated by jOOQ.
 */
package com.katbrew.entities.jooq.db.tables;


import com.katbrew.entities.jooq.db.Indexes;
import com.katbrew.entities.jooq.db.Keys;
import com.katbrew.entities.jooq.db.Public;
import com.katbrew.entities.jooq.db.tables.CodeWordings.CodeWordingsPath;
import com.katbrew.entities.jooq.db.tables.Holder.HolderPath;
import com.katbrew.entities.jooq.db.tables.Token.TokenPath;
import com.katbrew.entities.jooq.db.tables.records.TransactionRecord;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
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
import org.jooq.impl.AutoConverter;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Transaction extends TableImpl<TransactionRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.Transaction</code>
     */
    public static final Transaction TRANSACTION = new Transaction();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TransactionRecord> getRecordType() {
        return TransactionRecord.class;
    }

    /**
     * The column <code>public.Transaction.id</code>.
     */
    public final TableField<TransactionRecord, BigInteger> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "", new AutoConverter<Long, BigInteger>(Long.class, BigInteger.class));

    /**
     * The column <code>public.Transaction.fk_token</code>.
     */
    public final TableField<TransactionRecord, Integer> FK_TOKEN = createField(DSL.name("fk_token"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>public.Transaction.hash_rev</code>.
     */
    public final TableField<TransactionRecord, String> HASH_REV = createField(DSL.name("hash_rev"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.Transaction.p</code>.
     */
    public final TableField<TransactionRecord, Integer> P = createField(DSL.name("p"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>public.Transaction.op</code>.
     */
    public final TableField<TransactionRecord, Integer> OP = createField(DSL.name("op"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>public.Transaction.amt</code>.
     */
    public final TableField<TransactionRecord, BigInteger> AMT = createField(DSL.name("amt"), SQLDataType.CLOB, this, "", new AutoConverter<String, BigInteger>(String.class, BigInteger.class));

    /**
     * The column <code>public.Transaction.from_address</code>.
     */
    public final TableField<TransactionRecord, BigInteger> FROM_ADDRESS = createField(DSL.name("from_address"), SQLDataType.BIGINT, this, "", new AutoConverter<Long, BigInteger>(Long.class, BigInteger.class));

    /**
     * The column <code>public.Transaction.to_address</code>.
     */
    public final TableField<TransactionRecord, BigInteger> TO_ADDRESS = createField(DSL.name("to_address"), SQLDataType.BIGINT, this, "", new AutoConverter<Long, BigInteger>(Long.class, BigInteger.class));

    /**
     * The column <code>public.Transaction.op_score</code>.
     */
    public final TableField<TransactionRecord, BigInteger> OP_SCORE = createField(DSL.name("op_score"), SQLDataType.BIGINT, this, "", new AutoConverter<Long, BigInteger>(Long.class, BigInteger.class));

    /**
     * The column <code>public.Transaction.fee_rev</code>.
     */
    public final TableField<TransactionRecord, String> FEE_REV = createField(DSL.name("fee_rev"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.Transaction.tx_accept</code>.
     */
    public final TableField<TransactionRecord, String> TX_ACCEPT = createField(DSL.name("tx_accept"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.Transaction.op_accept</code>.
     */
    public final TableField<TransactionRecord, String> OP_ACCEPT = createField(DSL.name("op_accept"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.Transaction.op_error</code>.
     */
    public final TableField<TransactionRecord, Integer> OP_ERROR = createField(DSL.name("op_error"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>public.Transaction.checkpoint</code>.
     */
    public final TableField<TransactionRecord, String> CHECKPOINT = createField(DSL.name("checkpoint"), SQLDataType.CLOB, this, "");

    /**
     * The column <code>public.Transaction.mts_add</code>.
     */
    public final TableField<TransactionRecord, BigInteger> MTS_ADD = createField(DSL.name("mts_add"), SQLDataType.BIGINT, this, "", new AutoConverter<Long, BigInteger>(Long.class, BigInteger.class));

    /**
     * The column <code>public.Transaction.mts_mod</code>.
     */
    public final TableField<TransactionRecord, BigInteger> MTS_MOD = createField(DSL.name("mts_mod"), SQLDataType.BIGINT, this, "", new AutoConverter<Long, BigInteger>(Long.class, BigInteger.class));

    private Transaction(Name alias, Table<TransactionRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Transaction(Name alias, Table<TransactionRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>public.Transaction</code> table reference
     */
    public Transaction(String alias) {
        this(DSL.name(alias), TRANSACTION);
    }

    /**
     * Create an aliased <code>public.Transaction</code> table reference
     */
    public Transaction(Name alias) {
        this(alias, TRANSACTION);
    }

    /**
     * Create a <code>public.Transaction</code> table reference
     */
    public Transaction() {
        this(DSL.name("Transaction"), null);
    }

    public <O extends Record> Transaction(Table<O> path, ForeignKey<O, TransactionRecord> childPath, InverseForeignKey<O, TransactionRecord> parentPath) {
        super(path, childPath, parentPath, TRANSACTION);
    }

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    public static class TransactionPath extends Transaction implements Path<TransactionRecord> {

        private static final long serialVersionUID = 1L;
        public <O extends Record> TransactionPath(Table<O> path, ForeignKey<O, TransactionRecord> childPath, InverseForeignKey<O, TransactionRecord> parentPath) {
            super(path, childPath, parentPath);
        }
        private TransactionPath(Name alias, Table<TransactionRecord> aliased) {
            super(alias, aliased);
        }

        @Override
        public TransactionPath as(String alias) {
            return new TransactionPath(DSL.name(alias), this);
        }

        @Override
        public TransactionPath as(Name alias) {
            return new TransactionPath(alias, this);
        }

        @Override
        public TransactionPath as(Table<?> alias) {
            return new TransactionPath(alias.getQualifiedName(), this);
        }
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.TRANSACTION_FK_INDEX, Indexes.TRANSACTION_INDEX_OP, Indexes.TRANSACTION_INDEX_OP_SCORE);
    }

    @Override
    public Identity<TransactionRecord, BigInteger> getIdentity() {
        return (Identity<TransactionRecord, BigInteger>) super.getIdentity();
    }

    @Override
    public UniqueKey<TransactionRecord> getPrimaryKey() {
        return Keys.PK_TRANSACTION;
    }

    @Override
    public List<UniqueKey<TransactionRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.TRANSACTION_HASH_REV_KEY);
    }

    @Override
    public List<ForeignKey<TransactionRecord, ?>> getReferences() {
        return Arrays.asList(Keys.TRANSACTION__FK_TRANSACTION_HOLDER_FROM, Keys.TRANSACTION__FK_TRANSACTION_HOLDER_TO, Keys.TRANSACTION__FK_TRANSACTION_OP_CODES_WORDINGS, Keys.TRANSACTION__FK_TRANSACTION_OP_ERROR_CODES_WORDINGS, Keys.TRANSACTION__FK_TRANSACTION_P_CODES_WORDINGS, Keys.TRANSACTION__FK_TRANSACTION_TOKEN);
    }

    private transient HolderPath _fkTransactionHolderFrom;

    /**
     * Get the implicit join path to the <code>public.Holder</code> table, via
     * the <code>fk_transaction_holder_from</code> key.
     */
    public HolderPath fkTransactionHolderFrom() {
        if (_fkTransactionHolderFrom == null)
            _fkTransactionHolderFrom = new HolderPath(this, Keys.TRANSACTION__FK_TRANSACTION_HOLDER_FROM, null);

        return _fkTransactionHolderFrom;
    }

    private transient HolderPath _fkTransactionHolderTo;

    /**
     * Get the implicit join path to the <code>public.Holder</code> table, via
     * the <code>fk_transaction_holder_to</code> key.
     */
    public HolderPath fkTransactionHolderTo() {
        if (_fkTransactionHolderTo == null)
            _fkTransactionHolderTo = new HolderPath(this, Keys.TRANSACTION__FK_TRANSACTION_HOLDER_TO, null);

        return _fkTransactionHolderTo;
    }

    private transient CodeWordingsPath _fkTransactionOpCodesWordings;

    /**
     * Get the implicit join path to the <code>public.code_wordings</code>
     * table, via the <code>fk_transaction_op_codes_wordings</code> key.
     */
    public CodeWordingsPath fkTransactionOpCodesWordings() {
        if (_fkTransactionOpCodesWordings == null)
            _fkTransactionOpCodesWordings = new CodeWordingsPath(this, Keys.TRANSACTION__FK_TRANSACTION_OP_CODES_WORDINGS, null);

        return _fkTransactionOpCodesWordings;
    }

    private transient CodeWordingsPath _fkTransactionOpErrorCodesWordings;

    /**
     * Get the implicit join path to the <code>public.code_wordings</code>
     * table, via the <code>fk_transaction_op_error_codes_wordings</code> key.
     */
    public CodeWordingsPath fkTransactionOpErrorCodesWordings() {
        if (_fkTransactionOpErrorCodesWordings == null)
            _fkTransactionOpErrorCodesWordings = new CodeWordingsPath(this, Keys.TRANSACTION__FK_TRANSACTION_OP_ERROR_CODES_WORDINGS, null);

        return _fkTransactionOpErrorCodesWordings;
    }

    private transient CodeWordingsPath _fkTransactionPCodesWordings;

    /**
     * Get the implicit join path to the <code>public.code_wordings</code>
     * table, via the <code>fk_transaction_p_codes_wordings</code> key.
     */
    public CodeWordingsPath fkTransactionPCodesWordings() {
        if (_fkTransactionPCodesWordings == null)
            _fkTransactionPCodesWordings = new CodeWordingsPath(this, Keys.TRANSACTION__FK_TRANSACTION_P_CODES_WORDINGS, null);

        return _fkTransactionPCodesWordings;
    }

    private transient TokenPath _token;

    /**
     * Get the implicit join path to the <code>public.Token</code> table.
     */
    public TokenPath token() {
        if (_token == null)
            _token = new TokenPath(this, Keys.TRANSACTION__FK_TRANSACTION_TOKEN, null);

        return _token;
    }

    @Override
    public Transaction as(String alias) {
        return new Transaction(DSL.name(alias), this);
    }

    @Override
    public Transaction as(Name alias) {
        return new Transaction(alias, this);
    }

    @Override
    public Transaction as(Table<?> alias) {
        return new Transaction(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Transaction rename(String name) {
        return new Transaction(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Transaction rename(Name name) {
        return new Transaction(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Transaction rename(Table<?> name) {
        return new Transaction(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Transaction where(Condition condition) {
        return new Transaction(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Transaction where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Transaction where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Transaction where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Transaction where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Transaction where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Transaction where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Transaction where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Transaction whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Transaction whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
