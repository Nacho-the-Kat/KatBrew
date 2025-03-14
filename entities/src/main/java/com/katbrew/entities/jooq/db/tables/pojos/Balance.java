/*
 * This file is generated by jOOQ.
 */
package com.katbrew.entities.jooq.db.tables.pojos;


import com.katbrew.entities.jooq.db.tables.interfaces.IBalance;

import java.math.BigInteger;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Balance implements IBalance {

    private static final long serialVersionUID = 1L;

    private BigInteger id;
    private BigInteger holderId;
    private BigInteger balance;
    private Integer fkToken;

    public Balance() {}

    public Balance(IBalance value) {
        this.id = value.getId();
        this.holderId = value.getHolderId();
        this.balance = value.getBalance();
        this.fkToken = value.getFkToken();
    }

    public Balance(
        BigInteger id,
        BigInteger holderId,
        BigInteger balance,
        Integer fkToken
    ) {
        this.id = id;
        this.holderId = holderId;
        this.balance = balance;
        this.fkToken = fkToken;
    }

    /**
     * Getter for <code>public.Balance.id</code>.
     */
    @Override
    public BigInteger getId() {
        return this.id;
    }

    /**
     * Setter for <code>public.Balance.id</code>.
     */
    @Override
    public void setId(BigInteger id) {
        this.id = id;
    }

    /**
     * Getter for <code>public.Balance.holder_id</code>.
     */
    @Override
    public BigInteger getHolderId() {
        return this.holderId;
    }

    /**
     * Setter for <code>public.Balance.holder_id</code>.
     */
    @Override
    public void setHolderId(BigInteger holderId) {
        this.holderId = holderId;
    }

    /**
     * Getter for <code>public.Balance.balance</code>.
     */
    @Override
    public BigInteger getBalance() {
        return this.balance;
    }

    /**
     * Setter for <code>public.Balance.balance</code>.
     */
    @Override
    public void setBalance(BigInteger balance) {
        this.balance = balance;
    }

    /**
     * Getter for <code>public.Balance.fk_token</code>.
     */
    @Override
    public Integer getFkToken() {
        return this.fkToken;
    }

    /**
     * Setter for <code>public.Balance.fk_token</code>.
     */
    @Override
    public void setFkToken(Integer fkToken) {
        this.fkToken = fkToken;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Balance other = (Balance) obj;
        if (this.id == null) {
            if (other.id != null)
                return false;
        }
        else if (!this.id.equals(other.id))
            return false;
        if (this.holderId == null) {
            if (other.holderId != null)
                return false;
        }
        else if (!this.holderId.equals(other.holderId))
            return false;
        if (this.balance == null) {
            if (other.balance != null)
                return false;
        }
        else if (!this.balance.equals(other.balance))
            return false;
        if (this.fkToken == null) {
            if (other.fkToken != null)
                return false;
        }
        else if (!this.fkToken.equals(other.fkToken))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.holderId == null) ? 0 : this.holderId.hashCode());
        result = prime * result + ((this.balance == null) ? 0 : this.balance.hashCode());
        result = prime * result + ((this.fkToken == null) ? 0 : this.fkToken.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Balance (");

        sb.append(id);
        sb.append(", ").append(holderId);
        sb.append(", ").append(balance);
        sb.append(", ").append(fkToken);

        sb.append(")");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(IBalance from) {
        setId(from.getId());
        setHolderId(from.getHolderId());
        setBalance(from.getBalance());
        setFkToken(from.getFkToken());
    }

    @Override
    public <E extends IBalance> E into(E into) {
        into.from(this);
        return into;
    }
}
