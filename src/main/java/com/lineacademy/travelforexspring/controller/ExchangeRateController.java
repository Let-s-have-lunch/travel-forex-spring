package com.lineacademy.travelforexspring.controller;


import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import com.lineacademy.travelforexspring.dto.exchangerate.response.ExchangeRateResponse;
import com.lineacademy.travelforexspring.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/exchange-rates")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    /**
     * 특정 외화 금액을 원화(KRW)로 얼마인지 계산해서 반환합니다.
     * 예: GET /exchange-rates/calculate?currency=USD&amount=100
     */
    @GetMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculateRate(
            @RequestParam CurrencyCode currency,
            @RequestParam BigDecimal amount
    ) {
        try {
            ExchangeRateResponse response = exchangeRateService.calculateKrw(currency, amount);

            return ResponseEntity.ok(Map.of(
                    "message", "환율 계산 완료",
                    "data", response
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("RATE_NOT_AVAILABLE")) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(Map.of("message", "현재 해당 통화의 환율 정보를 불러올 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 에러"));
        }
    }
}