package com.katbrew.workflows.helper;

import lombok.Data;

@Data
public class ParsingResponse<T> {
    Boolean success;
    T result;
}
