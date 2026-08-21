package com.lineacademy.travelforexspring.dto.trip.response;

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

    public static TripResponse from(Trip trip) {
        return TripResponse.builder()
                .id(trip.getId())
                .title(trip.getTitle())
                .startDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .budgetKrw(trip.getBudgetKrw())
                .currency(trip.getCurrency())
                .build();
    }
}
