package com.katbrew.services.base;

import lombok.Data;

@Data
public class ApiResponse<T> {
    T result;
    Integer cursor;

    public ApiResponse(T result) {
        this.result = result;
    }

    public ApiResponse(T result, Integer cursor) {
        this.result = result;
        this.cursor = cursor;
    }

}
