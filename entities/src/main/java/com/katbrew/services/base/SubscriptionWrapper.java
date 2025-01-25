package com.katbrew.services.base;

import lombok.AllArgsConstructor;

import java.io.Serializable;


@AllArgsConstructor
public class SubscriptionWrapper<T extends Serializable> {
    String table;
    String method;
    T content;
}
