package com.lineacademy.travelforexspring.dto.general.exchangerate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class ExchangeRateSummaryResponse {
    private CurrencyCode currencyCode;
    private BigDecimal currentRate;
    private BigDecimal changeRate;

    @JsonProperty("isUp")
    private boolean isUp;

    private List<RatePointResponse> history;
}