package com.katbrew.pojos;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Map;

@Data
public class NftTransactionExternal implements Serializable {
    private BigInteger id;
    private String p;
    private String op;
    private String deployer;
    private String to;
    private String tick;
    private OpData opData;
    private BigInteger opScore;
    private String txIdRev;
    private String opError;
    private BigInteger mtsAdd;
    private String feeRev;

    @Data
    public static class OpData {
        String buri;
        Integer max;
        BigInteger tokenId;
        String to;
        BigInteger fee;
        Map<String, Object> royalty;
    }
}