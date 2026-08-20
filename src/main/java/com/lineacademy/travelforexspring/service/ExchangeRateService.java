package com.lineacademy.travelforexspring.service;

import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import com.lineacademy.travelforexspring.dto.exchangerate.response.ExchangeRateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final RestTemplate restTemplate;

    // TODO: 실제 발급받은 API KEY로 교체해야 합니다.
    private static final String API_KEY = "YOUR_API_KEY_HERE";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/KRW";

    // 환율 정보를 메모리에 임시 저장 (캐싱)
    private final Map<CurrencyCode, BigDecimal> exchangeRateCache = new ConcurrentHashMap<>();
    private LocalDateTime lastUpdated = LocalDateTime.MIN;

    /**
     * 외부 API에서 최신 환율을 가져와 캐시를 갱신합니다.
     * (1시간 이내에 이미 갱신했다면 기존 캐시를 사용해 API 호출을 아낍니다)
     */
    public void fetchLatestRates() {
        if (LocalDateTime.now().minusHours(1).isBefore(lastUpdated)) {
            return; // 1시간 이내 갱신됨 -> API 호출 생략
        }

        try {
            // 외부 API 호출
            Map<String, Object> response = restTemplate.getForObject(API_URL, Map.class);

            if (response != null && "success".equals(response.get("result"))) {
                Map<String, Number> conversionRates = (Map<String, Number>) response.get("conversion_rates");

                // API는 1 KRW = ? USD 형태로 주므로 역산해서 1 USD = ? KRW 로 변환하여 저장
                for (CurrencyCode currency : CurrencyCode.values()) {
                    if (currency == CurrencyCode.KRW) continue;

                    if (conversionRates.containsKey(currency.name())) {
                        double ratePerKrw = conversionRates.get(currency.name()).doubleValue();
                        // 1 / rate = 1 외화당 원화 가격
                        BigDecimal krwRate = BigDecimal.ONE.divide(BigDecimal.valueOf(ratePerKrw), 4, RoundingMode.HALF_UP);
                        exchangeRateCache.put(currency, krwRate);
                    }
                }
                lastUpdated = LocalDateTime.now();
            }
        } catch (Exception e) {
            System.err.println("환율 정보를 가져오는 데 실패했습니다: " + e.getMessage());
            // TODO: 실패 시 DB에 저장해둔 마지막 환율을 불러오는 등의 Fallback 로직 필요
        }
    }

    /**
     * 특정 통화의 현재 환율을 조회하고, 입력된 금액을 원화로 계산합니다.
     */
    public ExchangeRateResponse calculateKrw(CurrencyCode currency, BigDecimal amount) {
        if (currency == CurrencyCode.KRW) {
            return ExchangeRateResponse.builder()
                    .targetCurrency(CurrencyCode.KRW)
                    .baseRate(BigDecimal.ONE)
                    .inputAmount(amount)
                    .convertedKrw(amount)
                    .build();
        }

        // 캐시 갱신 확인
        fetchLatestRates();

        // 캐시에 환율이 없으면 임시로 기본값(또는 예외) 처리
        BigDecimal currentRate = exchangeRateCache.getOrDefault(currency, BigDecimal.ZERO);
        if (currentRate.compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException("RATE_NOT_AVAILABLE");
        }

        // 외화 * 적용환율 = 원화
        BigDecimal convertedKrw = amount.multiply(currentRate).setScale(0, RoundingMode.HALF_UP);

        return ExchangeRateResponse.builder()
                .targetCurrency(currency)
                .baseRate(currentRate)
                .inputAmount(amount)
                .convertedKrw(convertedKrw)
                .build();
    }
}
