package com.katbrew.workflows.helper;

import lombok.Data;

import java.math.BigInteger;

@Data
public class ParsingResponse<T> {
    T result;
    BigInteger next;
}
