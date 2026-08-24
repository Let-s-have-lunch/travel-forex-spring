package com.lineacademy.travelforexspring.initializer;

import com.lineacademy.travelforexspring.client.FrankfurterHistoricalRateClient;
import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import com.lineacademy.travelforexspring.domain.exchangerate.ExchangeRateHistory;
import com.lineacademy.travelforexspring.repository.ExchangeRateHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 애플리케이션 시작 시, 통화별 환율 이력이 충분한지 확인하고
 * 부족하면 Frankfurter API(ECB 기반, 무료)의 실제 과거 환율로 채워 넣습니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeRateHistorySeeder implements ApplicationRunner {

    private static final int BACKFILL_DAYS = 365; // 30 → 365로 확장
    private static final long MIN_REQUIRED_COUNT = 180; // 1년 중 절반 이상 영업일

    private final ExchangeRateHistoryRepository exchangeRateHistoryRepository;
    private final FrankfurterHistoricalRateClient frankfurterHistoricalRateClient;

    @Override
    public void run(ApplicationArguments args) {
        LocalDateTime checkFrom = LocalDateTime.now().minusDays(BACKFILL_DAYS);

        for (CurrencyCode currency : CurrencyCode.values()) {
            if (currency == CurrencyCode.KRW) continue;

            long existingCount = exchangeRateHistoryRepository
                    .countByCurrencyCodeAndRecordedAtAfter(currency, checkFrom);

            if (existingCount >= MIN_REQUIRED_COUNT) {
                log.info("[ExchangeRateHistorySeeder] {} 이력 충분 (count={}), 백필 스킵", currency, existingCount);
                continue;
            }

            backfillFrom(currency);
        }
    }

    private void backfillFrom(CurrencyCode currency) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(BACKFILL_DAYS);

        Map<LocalDate, BigDecimal> dailyRates =
                frankfurterHistoricalRateClient.fetchDailyKrwRates(currency, start, end);

        if (dailyRates.isEmpty()) {
            log.warn("[ExchangeRateHistorySeeder] {} 과거 데이터 조회 실패, 백필 스킵", currency);
            return;
        }

        List<ExchangeRateHistory> histories = new ArrayList<>();
        dailyRates.forEach((date, rate) -> histories.add(
                ExchangeRateHistory.builder()
                        .currencyCode(currency)
                        .rate(rate)
                        // ECB 고시 시각(대략 CET 16:00) 기준으로 통일해 시간대 저장
                        .recordedAt(date.atTime(LocalTime.of(16, 0)))
                        .build()
        ));

        exchangeRateHistoryRepository.saveAll(histories);
        log.info("[ExchangeRateHistorySeeder] {} 실제 이력 {}건 백필 완료 (source=Frankfurter/ECB)", currency, histories.size());
    }
}
