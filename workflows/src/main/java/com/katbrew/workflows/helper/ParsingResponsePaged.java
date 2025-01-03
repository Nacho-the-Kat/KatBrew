package com.katbrew.workflows.helper;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;

@EqualsAndHashCode(callSuper = true)
@Data
public class ParsingResponsePaged<T> extends ParsingResponse<T> {
    Pagination pagination;

    @Data
    public static class Pagination {
        Integer currentPage;
        Integer pageSize;
        Integer totalPages;
        BigInteger totalRecords;
    }
}
