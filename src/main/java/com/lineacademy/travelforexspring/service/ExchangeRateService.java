package com.lineacademy.travelforexspring.service;

import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import com.lineacademy.travelforexspring.domain.exchangerate.ExchangeRate;
import com.lineacademy.travelforexspring.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    @Transactional(readOnly = true)
    public ExchangeRate getLatestRate(CurrencyCode currency) {
        return exchangeRateRepository.findTopByCurrencyOrderByRecordDateDesc(currency)
                .orElseThrow(() -> new RuntimeException("RATE_NOT_FOUND"));
    }

    @Transactional(readOnly = true)
    public List<ExchangeRate> getChartRates(CurrencyCode currency, String period) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate;

        // 탭(기간)에 따른 조회 시작일 계산
        switch (period.toUpperCase()) {
            case "1D": startDate = endDate.minusDays(1); break;
            case "1W": startDate = endDate.minusWeeks(1); break;
            case "1M": startDate = endDate.minusMonths(1); break;
            case "3M": startDate = endDate.minusMonths(3); break;
            case "1Y": startDate = endDate.minusYears(1); break;
            default: throw new RuntimeException("INVALID_PERIOD");
        }

        return exchangeRateRepository.findAllByCurrencyAndRecordDateBetweenOrderByRecordDateAsc(
                currency, startDate, endDate);
    }
}
