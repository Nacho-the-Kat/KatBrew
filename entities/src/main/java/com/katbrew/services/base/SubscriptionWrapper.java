package com.katbrew.services.base;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;


@Data
@AllArgsConstructor
public class SubscriptionWrapper<T extends Serializable> {
    String table;
    String method;
    T content;
}
