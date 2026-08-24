package com.lineacademy.travelforexspring.service;

import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import com.lineacademy.travelforexspring.domain.enums.RatePeriod;
import com.lineacademy.travelforexspring.domain.exchangerate.ExchangeRateHistory;
import com.lineacademy.travelforexspring.dto.general.exchangerate.ExchangeRateResponse;
import com.lineacademy.travelforexspring.dto.general.exchangerate.ExchangeRateSummaryResponse;
import com.lineacademy.travelforexspring.dto.general.exchangerate.RatePointResponse;
import com.lineacademy.travelforexspring.repository.ExchangeRateHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {
    private final RestTemplate restTemplate;
    private final ExchangeRateHistoryRepository exchangeRateHistoryRepository;

    @Value("${exchange-rate.api-key}")
    private String apiKey;

    // 외부 API 호출 결과를 잠깐 담아두는 실시간 캐시 (계산 API 응답 속도용)
    private final Map<CurrencyCode, BigDecimal> exchangeRateCache = new ConcurrentHashMap<>();
    private LocalDateTime lastFetchedAt = LocalDateTime.MIN;

    /**
     * 외부 API에서 최신 환율을 가져와 인메모리 캐시를 갱신합니다.
     * 1시간 이내에 이미 갱신했다면 스킵합니다.
     */
    public void fetchLatestRates() {
        if (LocalDateTime.now().minusHours(1).isBefore(lastFetchedAt)) {
            return;
        }

        String apiUrl = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/KRW";

        try {
            Map response = restTemplate.getForObject(apiUrl, Map.class);

            if (response != null && "success".equals(response.get("result"))) {
                Map<String, Number> conversionRates = (Map<String, Number>) response.get("conversion_rates");

                for (CurrencyCode currency : CurrencyCode.values()) {
                    if (currency == CurrencyCode.KRW) continue;

                    if (conversionRates.containsKey(currency.name())) {
                        double ratePerKrw = conversionRates.get(currency.name()).doubleValue();
                        BigDecimal krwRate = BigDecimal.ONE.divide(BigDecimal.valueOf(ratePerKrw), 4, RoundingMode.HALF_UP);
                        exchangeRateCache.put(currency, krwRate);
                    }
                }
                lastFetchedAt = LocalDateTime.now();
            }
        } catch (Exception e) {
            System.err.println("환율 정보를 가져오는 데 실패했습니다: " + e.getMessage());
        }
    }

    public ExchangeRateResponse calculateKrw(CurrencyCode currency, BigDecimal amount) {
        if (currency == CurrencyCode.KRW) {
            return ExchangeRateResponse.builder()
                    .targetCurrency(CurrencyCode.KRW)
                    .baseRate(BigDecimal.ONE)
                    .inputAmount(amount)
                    .convertedKrw(amount)
                    .build();
        }

        fetchLatestRates();

        BigDecimal currentRate = exchangeRateCache.getOrDefault(currency, BigDecimal.ZERO);
        if (currentRate.compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException("RATE_NOT_AVAILABLE");
        }

        BigDecimal convertedKrw = amount.multiply(currentRate).setScale(0, RoundingMode.HALF_UP);

        return ExchangeRateResponse.builder()
                .targetCurrency(currency)
                .baseRate(currentRate)
                .inputAmount(amount)
                .convertedKrw(convertedKrw)
                .build();
    }

    /**
     * 특정 통화의 현재 환율을 조회합니다. (스케줄러에서 DB 스냅샷 저장 시 사용)
     */
    public Optional<BigDecimal> getCurrentRate(CurrencyCode currency) {
        if (currency == CurrencyCode.KRW) {
            return Optional.of(BigDecimal.ONE);
        }

        fetchLatestRates();

        BigDecimal rate = exchangeRateCache.get(currency);
        return Optional.ofNullable(rate);
    }

    /**
     * DB에 쌓인 이력을 기반으로 통화별 현재가 + 등락률 + 시계열 데이터를 반환합니다.
     */
    public List<ExchangeRateSummaryResponse> getSummary(List<CurrencyCode> currencies, RatePeriod period) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = switch (period) {
            case ONE_DAY -> end.minusDays(1);
            case ONE_WEEK -> end.minusWeeks(1);
            case ONE_MONTH -> end.minusMonths(1);
            case THREE_MONTHS -> end.minusMonths(3);
            case ONE_YEAR -> end.minusYears(1);
        };

        return currencies.stream()
                .map(currency -> buildSummary(currency, start, end))
                .toList();
    }

    private ExchangeRateSummaryResponse buildSummary(CurrencyCode currency, LocalDateTime start, LocalDateTime end) {
        List<ExchangeRateHistory> histories =
                exchangeRateHistoryRepository.findByCurrencyCodeAndRecordedAtBetweenOrderByRecordedAtAsc(
                        currency, start, end);

        if (histories.isEmpty()) {
            throw new RuntimeException("RATE_NOT_AVAILABLE");
        }

        BigDecimal firstRate = histories.get(0).getRate();
        BigDecimal lastRate = histories.get(histories.size() - 1).getRate();

        BigDecimal changeRate = lastRate.subtract(firstRate)
                .divide(firstRate, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        List<RatePointResponse> points = histories.stream()
                .map(h -> RatePointResponse.builder()
                        .recordedAt(h.getRecordedAt())
                        .rate(h.getRate())
                        .build())
                .toList();

        return ExchangeRateSummaryResponse.builder()
                .currencyCode(currency)
                .currentRate(lastRate)
                .changeRate(changeRate.abs())
                .isUp(changeRate.compareTo(BigDecimal.ZERO) >= 0)
                .history(points)
                .build();
    }
}