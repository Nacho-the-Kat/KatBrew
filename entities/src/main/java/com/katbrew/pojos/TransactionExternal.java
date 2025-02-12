package com.katbrew.pojos;

import com.katbrew.entities.jooq.db.tables.pojos.Transaction;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigInteger;

@Data
public class TransactionExternal implements Serializable {
    String from;
    String to;
    BigInteger fromAddress;
    BigInteger toAddress;
    String tick;
    Integer fkToken;
    String opError;
    String hashRev;
    String p;
    String op;
    BigInteger amt;
    BigInteger opScore;
    String feeRev;
    String txAccept;
    String opAccept;
    String checkpoint;
    BigInteger mtsAdd;
    BigInteger mtsMod;
}