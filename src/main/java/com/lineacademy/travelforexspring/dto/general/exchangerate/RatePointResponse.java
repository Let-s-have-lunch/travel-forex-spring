package com.lineacademy.travelforexspring.dto.general.exchangerate;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class RatePointResponse {
    private LocalDateTime recordedAt;
    private BigDecimal rate;
}