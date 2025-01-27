package com.katbrew.pojos;

import lombok.Data;

import java.math.BigInteger;

@Data
public class TokenHolder {
    String address;
    BigInteger balance;
}