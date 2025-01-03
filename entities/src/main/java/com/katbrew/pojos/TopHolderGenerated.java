package com.katbrew.pojos;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class TopHolderGenerated {
    BigInteger id;
    String address;
    List<TopHolderBalance> balances;
    Integer tokenCount;

}
