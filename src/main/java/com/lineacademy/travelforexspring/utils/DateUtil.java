package com.lineacademy.travelforexspring.utils;

import java.time.LocalDate;

public final class DateUtil {
    private DateUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        // null 체크를 포함하여 안전하게 검증합니다.
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new RuntimeException("INVALID_DATE_RANGE");
        }
    }
}
