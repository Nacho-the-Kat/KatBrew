package com.katbrew.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Data
@AllArgsConstructor
public class TopHolder {
    String address;
    List<Balances> balances;

    @Data
    @AllArgsConstructor
    public static class Balances {
        String tick;
        BigInteger balance;
    }
}
