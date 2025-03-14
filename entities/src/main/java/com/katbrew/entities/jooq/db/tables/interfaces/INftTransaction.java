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
public interface INftTransaction extends Serializable {

    /**
     * Setter for <code>public.nft_transaction.id</code>.
     */
    public void setId(BigInteger value);

    /**
     * Getter for <code>public.nft_transaction.id</code>.
     */
    public BigInteger getId();

    /**
     * Setter for <code>public.nft_transaction.p</code>.
     */
    public void setP(String value);

    /**
     * Getter for <code>public.nft_transaction.p</code>.
     */
    public String getP();

    /**
     * Setter for <code>public.nft_transaction.op</code>.
     */
    public void setOp(String value);

    /**
     * Getter for <code>public.nft_transaction.op</code>.
     */
    public String getOp();

    /**
     * Setter for <code>public.nft_transaction.deployer</code>.
     */
    public void setDeployer(BigInteger value);

    /**
     * Getter for <code>public.nft_transaction.deployer</code>.
     */
    public BigInteger getDeployer();

    /**
     * Setter for <code>public.nft_transaction.to_address</code>.
     */
    public void setToAddress(BigInteger value);

    /**
     * Getter for <code>public.nft_transaction.to_address</code>.
     */
    public BigInteger getToAddress();

    /**
     * Setter for <code>public.nft_transaction.fk_nft_collection</code>.
     */
    public void setFkNftCollection(BigInteger value);

    /**
     * Getter for <code>public.nft_transaction.fk_nft_collection</code>.
     */
    public BigInteger getFkNftCollection();

    /**
     * Setter for <code>public.nft_transaction.transaction_tick</code>.
     */
    public void setTransactionTick(String value);

    /**
     * Getter for <code>public.nft_transaction.transaction_tick</code>.
     */
    public String getTransactionTick();

    /**
     * Setter for <code>public.nft_transaction.op_data</code>.
     */
    public void setOpData(String value);

    /**
     * Getter for <code>public.nft_transaction.op_data</code>.
     */
    public String getOpData();

    /**
     * Setter for <code>public.nft_transaction.op_score</code>.
     */
    public void setOpScore(BigInteger value);

    /**
     * Getter for <code>public.nft_transaction.op_score</code>.
     */
    public BigInteger getOpScore();

    /**
     * Setter for <code>public.nft_transaction.tx_id_rev</code>.
     */
    public void setTxIdRev(String value);

    /**
     * Getter for <code>public.nft_transaction.tx_id_rev</code>.
     */
    public String getTxIdRev();

    /**
     * Setter for <code>public.nft_transaction.op_error</code>.
     */
    public void setOpError(String value);

    /**
     * Getter for <code>public.nft_transaction.op_error</code>.
     */
    public String getOpError();

    /**
     * Setter for <code>public.nft_transaction.mts_add</code>.
     */
    public void setMtsAdd(BigInteger value);

    /**
     * Getter for <code>public.nft_transaction.mts_add</code>.
     */
    public BigInteger getMtsAdd();

    /**
     * Setter for <code>public.nft_transaction.fee_rev</code>.
     */
    public void setFeeRev(String value);

    /**
     * Getter for <code>public.nft_transaction.fee_rev</code>.
     */
    public String getFeeRev();

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    /**
     * Load data from another generated Record/POJO implementing the common
     * interface INftTransaction
     */
    public void from(INftTransaction from);

    /**
     * Copy data into another generated Record/POJO implementing the common
     * interface INftTransaction
     */
    public <E extends INftTransaction> E into(E into);
}
