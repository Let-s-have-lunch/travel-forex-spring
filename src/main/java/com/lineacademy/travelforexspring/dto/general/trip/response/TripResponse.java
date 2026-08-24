package com.lineacademy.travelforexspring.dto.general.trip.response;

import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import com.lineacademy.travelforexspring.domain.trip.Trip;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class TripResponse {
    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal budgetKrw;
    private CurrencyCode currency;
    private BigDecimal totalExpenseKrw; // 🆕 지출 총액 (KRW 환산 기준)

    public static TripResponse from(Trip trip, BigDecimal totalExpenseKrw) {
        return TripResponse.builder()
                .id(trip.getId())
                .title(trip.getTitle())
                .startDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .budgetKrw(trip.getBudgetKrw())
                .currency(trip.getCurrency())
                .totalExpenseKrw(totalExpenseKrw != null ? totalExpenseKrw : BigDecimal.ZERO)
                .build();
    }
}
