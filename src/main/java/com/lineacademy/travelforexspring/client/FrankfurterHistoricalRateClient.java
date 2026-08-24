package com.lineacademy.travelforexspring.client;

import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import com.lineacademy.travelforexspring.dto.external.FrankfurterTimeSeriesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

/**
 * Frankfurter API(ECB 기반, 무료, API 키 불필요)를 이용해
 * 특정 통화의 실제 과거 환율(1단위당 KRW)을 기간별로 조회합니다.
 */
@Slf4j
@Component
public class FrankfurterHistoricalRateClient {

    private static final String BASE_URL = "https://api.frankfurter.dev/v1";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final RestTemplate restTemplate;

    public FrankfurterHistoricalRateClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * @return 날짜별(오름차순) "1 {currency} = ? KRW" 맵. 조회 실패 시 빈 맵.
     */
    public Map<LocalDate, BigDecimal> fetchDailyKrwRates(CurrencyCode currency, LocalDate start, LocalDate end) {
        String url = String.format(
                "%s/%s..%s?base=%s&symbols=KRW",
                BASE_URL,
                start.format(DATE_FORMAT),
                end.format(DATE_FORMAT),
                currency.name()
        );

        Map<LocalDate, BigDecimal> result = new TreeMap<>();

        try {
            FrankfurterTimeSeriesResponse response = restTemplate.getForObject(url, FrankfurterTimeSeriesResponse.class);

            if (response == null || response.getRates() == null) {
                log.warn("[Frankfurter] {} 응답이 비어있음", currency);
                return result;
            }

            response.getRates().forEach((dateStr, rateMap) -> {
                BigDecimal krwRate = rateMap.get("KRW");
                if (krwRate != null) {
                    result.put(LocalDate.parse(dateStr), krwRate);
                }
            });
        } catch (Exception e) {
            log.error("[Frankfurter] {} 과거 환율 조회 실패: {}", currency, e.getMessage());
        }

        return result;
    }
}
