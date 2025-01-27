package com.katbrew.pojos;

import lombok.Data;

import java.math.BigInteger;

@Data
public class TokenList {
    private Integer id;
    private String tick;
    private BigInteger max;
    private BigInteger lim;
    private BigInteger pre;
    private BigInteger mtsAdd;
    private BigInteger minted;
    private Integer holderTotal;
    private Integer mintTotal;
    private BigInteger transferTotal;
    private Integer dec;
    private String state;
    private String logo;
    private String socials;
}
