package com.lineacademy.travelforexspring.dto.common;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PaginationResponse<T> {
    private int page;
    private int size;
    private long total;
    private List<T> list;

    public static <T> PaginationResponse<T> of(int page, int size, long total, List<T> list) {
        return PaginationResponse.<T>builder()
                .page(page)
                .size(size)
                .total(total)
                .list(list)
                .build();
    }
}
