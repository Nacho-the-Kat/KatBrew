package com.katbrew.workflows.helper;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ParsingResponsePaged<T> extends ParsingResponse<T> {
    Pagination pagination;

    @Data
    public static class Pagination {
        Integer pageSize;
        Boolean hasMore = false;
    }
}
