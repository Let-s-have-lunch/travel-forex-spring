package com.lineacademy.travelforexspring.dto.exchangerate.response;


import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ExchangeRateResponse {
    private CurrencyCode targetCurrency; // 대상 통화 (USD, JPY 등)
    private BigDecimal baseRate;         // 1 단위당 원화(KRW) 환율 (단, JPY는 보통 100엔 기준일 수 있으나 여기선 1단위로 통일)
    private BigDecimal inputAmount;      // 사용자가 입력한 외화 금액
    private BigDecimal convertedKrw;     // 환율이 적용된 원화 총액
}
