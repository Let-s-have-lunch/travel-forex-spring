package com.lineacademy.travelforexspring.service;

import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import com.lineacademy.travelforexspring.dto.general.exchangerate.ExchangeRateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${exchange-rate.api-key}")
    private String apiKey;

    private final Map<CurrencyCode, BigDecimal> exchangeRateCache = new ConcurrentHashMap<>();
    private LocalDateTime lastUpdated = LocalDateTime.MIN;

    public void fetchLatestRates() {
        if (LocalDateTime.now().minusHours(1).isBefore(lastUpdated)) {
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
                lastUpdated = LocalDateTime.now();
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
}