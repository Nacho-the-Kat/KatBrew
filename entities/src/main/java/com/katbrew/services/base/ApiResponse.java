package com.katbrew.services.base;

import lombok.Data;

@Data
public class ApiResponse<T> {
    Integer status = 200;
    String message;
    T result;
    Integer cursor;

    public ApiResponse(final T result) {
        this.result = result;
    }

    public ApiResponse(final T result, final Integer cursor) {
        this.result = result;
        this.cursor = cursor;
    }

    public ApiResponse(final Integer status, final String message) {
        this.status = status;
        this.message = message;
    }


}
