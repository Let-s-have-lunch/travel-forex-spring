package com.lineacademy.travelforexspring.controller;

import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import com.lineacademy.travelforexspring.domain.exchangerate.ExchangeRate;
import com.lineacademy.travelforexspring.dto.exchangerate.response.ExchangeRateResponse;
import com.lineacademy.travelforexspring.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exchange-rates")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    // 1. 최신 환율 조회 (입출금 등록 시 프론트에서 호출)
    @GetMapping("/{currency}/latest")
    public ResponseEntity<Map<String, Object>> getLatestRate(@PathVariable CurrencyCode currency) {
        try {
            ExchangeRate latestRate = exchangeRateService.getLatestRate(currency);
            return ResponseEntity.ok(Map.of(
                    "message", "최신 환율 조회 성공",
                    "data", ExchangeRateResponse.from(latestRate)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("RATE_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 통화의 환율 정보가 없습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    // 2. 차트용 과거 환율 리스트 조회 (8번 환율 탭에서 호출)
    @GetMapping("/{currency}/chart")
    public ResponseEntity<Map<String, Object>> getChartRates(
            @PathVariable CurrencyCode currency,
            @RequestParam(defaultValue = "1D") String period
    ) {
        try {
            List<ExchangeRate> chartRates = exchangeRateService.getChartRates(currency, period);
            List<ExchangeRateResponse> responses = chartRates.stream()
                    .map(ExchangeRateResponse::from)
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "message", period + " 차트 환율 조회 성공",
                    "data", responses
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("INVALID_PERIOD"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "유효하지 않은 기간 포맷입니다. (1D, 1W, 1M, 3M, 1Y)"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }
}
