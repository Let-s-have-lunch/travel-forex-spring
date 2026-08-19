package com.lineacademy.travelforexspring.dto.common;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CursorPaginationResponse<T> {

    private List<T> list;


    private boolean hasNext;


    private Long nextCursorId;

    public static <T> CursorPaginationResponse<T> of(List<T> list, boolean hasNext, Long nextCursorId) {
        return CursorPaginationResponse.<T>builder()
                .list(list)
                .hasNext(hasNext)
                .nextCursorId(nextCursorId)
                .build();
    }
}