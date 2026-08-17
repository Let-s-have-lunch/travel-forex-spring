package com.lineacademy.travelforexspring.dto.exchangerate.response;

import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import com.lineacademy.travelforexspring.domain.exchangerate.ExchangeRate;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ExchangeRateResponse {
    private Long id;
    private CurrencyCode currency;
    private BigDecimal baseRate;
    private BigDecimal changeRate;
    private LocalDateTime recordDate;

    public static ExchangeRateResponse from(ExchangeRate exchangeRate) {
        return ExchangeRateResponse.builder()
                .id(exchangeRate.getId())
                .currency(exchangeRate.getCurrency())
                .baseRate(exchangeRate.getBaseRate())
                .changeRate(exchangeRate.getChangeRate())
                .recordDate(exchangeRate.getRecordDate())
                .build();
    }
}
