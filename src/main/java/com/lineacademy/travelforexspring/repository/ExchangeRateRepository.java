package com.lineacademy.travelforexspring.repository;

import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import com.lineacademy.travelforexspring.domain.exchangerate.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    // 1. 특정 통화의 가장 최신 환율 1건 조회 (홈 화면, 등록 화면 등에서 사용)
    Optional<ExchangeRate> findTopByCurrencyOrderByRecordDateDesc(CurrencyCode currency);

    // 2. 기간별(1D, 1W, 1M 등) 차트 데이터를 위한 범위 조회
    // 조건에 맞는 데이터를 전부다(List형태로) 해당 통화 데이터로 필터링해서 가져오는데
    // 시작날짜와 종료날짜 사이에 기록된 환율만 가져오고
    // 과거 데이터부터 최신 데이터 순(오름차순) 으로 정렬해서 가져온다.
    List<ExchangeRate> findAllByCurrencyAndRecordDateBetweenOrderByRecordDateAsc(
            CurrencyCode currency, LocalDateTime startDate, LocalDateTime endDate);
}
