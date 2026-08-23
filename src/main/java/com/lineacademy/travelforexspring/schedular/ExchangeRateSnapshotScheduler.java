package com.lineacademy.travelforexspring.schedular;


import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import com.lineacademy.travelforexspring.domain.exchangerate.ExchangeRateHistory;
import com.lineacademy.travelforexspring.repository.ExchangeRateHistoryRepository;
import com.lineacademy.travelforexspring.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ExchangeRateSnapshotScheduler {

    private final ExchangeRateService exchangeRateService;
    private final ExchangeRateHistoryRepository exchangeRateHistoryRepository;

    // 10분마다 각 통화의 현재 환율을 DB에 스냅샷으로 저장
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void snapshotRates() {
        LocalDateTime now = LocalDateTime.now();

        for (CurrencyCode currency : CurrencyCode.values()) {
            if (currency == CurrencyCode.KRW) continue;

            exchangeRateService.getCurrentRate(currency).ifPresent(rate ->
                    exchangeRateHistoryRepository.save(
                            ExchangeRateHistory.builder()
                                    .currencyCode(currency)
                                    .rate(rate)
                                    .recordedAt(now)
                                    .build()
                    )
            );
        }
    }
}
