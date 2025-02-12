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
public interface IToken extends Serializable {

    /**
     * Setter for <code>public.Token.id</code>.
     */
    public void setId(Integer value);

    /**
     * Getter for <code>public.Token.id</code>.
     */
    public Integer getId();

    /**
     * Setter for <code>public.Token.tick</code>.
     */
    public void setTick(String value);

    /**
     * Getter for <code>public.Token.tick</code>.
     */
    public String getTick();

    /**
     * Setter for <code>public.Token.max</code>.
     */
    public void setMax(BigInteger value);

    /**
     * Getter for <code>public.Token.max</code>.
     */
    public BigInteger getMax();

    /**
     * Setter for <code>public.Token.lim</code>.
     */
    public void setLim(BigInteger value);

    /**
     * Getter for <code>public.Token.lim</code>.
     */
    public BigInteger getLim();

    /**
     * Setter for <code>public.Token.pre</code>.
     */
    public void setPre(BigInteger value);

    /**
     * Getter for <code>public.Token.pre</code>.
     */
    public BigInteger getPre();

    /**
     * Setter for <code>public.Token.mts_add</code>.
     */
    public void setMtsAdd(BigInteger value);

    /**
     * Getter for <code>public.Token.mts_add</code>.
     */
    public BigInteger getMtsAdd();

    /**
     * Setter for <code>public.Token.minted</code>.
     */
    public void setMinted(BigInteger value);

    /**
     * Getter for <code>public.Token.minted</code>.
     */
    public BigInteger getMinted();

    /**
     * Setter for <code>public.Token.holder_total</code>.
     */
    public void setHolderTotal(Integer value);

    /**
     * Getter for <code>public.Token.holder_total</code>.
     */
    public Integer getHolderTotal();

    /**
     * Setter for <code>public.Token.mint_total</code>.
     */
    public void setMintTotal(Integer value);

    /**
     * Getter for <code>public.Token.mint_total</code>.
     */
    public Integer getMintTotal();

    /**
     * Setter for <code>public.Token.transfer_total</code>.
     */
    public void setTransferTotal(BigInteger value);

    /**
     * Getter for <code>public.Token.transfer_total</code>.
     */
    public BigInteger getTransferTotal();

    /**
     * Setter for <code>public.Token.dec</code>.
     */
    public void setDec(Integer value);

    /**
     * Getter for <code>public.Token.dec</code>.
     */
    public Integer getDec();

    /**
     * Setter for <code>public.Token.state</code>.
     */
    public void setState(String value);

    /**
     * Getter for <code>public.Token.state</code>.
     */
    public String getState();

    /**
     * Setter for <code>public.Token.hash_rev</code>.
     */
    public void setHashRev(String value);

    /**
     * Getter for <code>public.Token.hash_rev</code>.
     */
    public String getHashRev();

    /**
     * Setter for <code>public.Token.op_score_mod</code>.
     */
    public void setOpScoreMod(BigInteger value);

    /**
     * Getter for <code>public.Token.op_score_mod</code>.
     */
    public BigInteger getOpScoreMod();

    /**
     * Setter for <code>public.Token.op_score_add</code>.
     */
    public void setOpScoreAdd(BigInteger value);

    /**
     * Getter for <code>public.Token.op_score_add</code>.
     */
    public BigInteger getOpScoreAdd();

    /**
     * Setter for <code>public.Token.to</code>.
     */
    public void setTo(String value);

    /**
     * Getter for <code>public.Token.to</code>.
     */
    public String getTo();

    /**
     * Setter for <code>public.Token.logo</code>.
     */
    public void setLogo(String value);

    /**
     * Getter for <code>public.Token.logo</code>.
     */
    public String getLogo();

    /**
     * Setter for <code>public.Token.socials</code>.
     */
    public void setSocials(String value);

    /**
     * Getter for <code>public.Token.socials</code>.
     */
    public String getSocials();

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    /**
     * Load data from another generated Record/POJO implementing the common
     * interface IToken
     */
    public void from(IToken from);

    /**
     * Copy data into another generated Record/POJO implementing the common
     * interface IToken
     */
    public <E extends IToken> E into(E into);
}
