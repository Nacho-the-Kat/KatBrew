package com.katbrew.pojos;

import lombok.Data;

import java.math.BigInteger;

@Data
public class TopHolderBalance {
    String tick;
    BigInteger amount;
}
