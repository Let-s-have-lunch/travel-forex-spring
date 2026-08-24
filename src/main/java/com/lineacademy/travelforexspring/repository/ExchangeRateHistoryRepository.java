package com.lineacademy.travelforexspring.repository;


import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import com.lineacademy.travelforexspring.domain.exchangerate.ExchangeRateHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ExchangeRateHistoryRepository extends JpaRepository<ExchangeRateHistory, Long> {

    List<ExchangeRateHistory> findByCurrencyCodeAndRecordedAtBetweenOrderByRecordedAtAsc(
            CurrencyCode currencyCode, LocalDateTime start, LocalDateTime end);

    long countByCurrencyCodeAndRecordedAtAfter(CurrencyCode currencyCode, LocalDateTime after);
}
