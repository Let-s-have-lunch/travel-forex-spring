package com.lineacademy.travelforexspring.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FrankfurterTimeSeriesResponse {
    private String base;
    // key: 날짜(yyyy-MM-dd), value: { 통화코드: 환율 }
    private Map<String, Map<String, BigDecimal>> rates;
}