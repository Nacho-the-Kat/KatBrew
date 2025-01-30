package com.katbrew.workflows.helper;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ParsingResponsePagedNFT<T> extends ParsingResponse<T> {
    String message;
    String prev;
    String next;
}
