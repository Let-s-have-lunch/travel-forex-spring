package com.lineacademy.travelforexspring.repository;


import com.lineacademy.travelforexspring.domain.tripexpense.TripExpense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TripExpenseRepository extends JpaRepository<TripExpense, Long> {
    // 특정 여행의 지출 내역을 최신 지출일 순으로 정렬하여 조회
    Page<TripExpense> findAllByTripIdOrderByExpenseDateDesc(Long tripId, Pageable pageable);

    Optional<TripExpense> findByIdAndTripId(Long id, Long tripId);

    // 단일 트립의 지출 총액 (KRW 환산 기준)
    @Query("select coalesce(sum(te.convertedKrwAmount), 0) " +
            "from TripExpense te where te.trip.id = :tripId")
    BigDecimal sumConvertedKrwAmountByTripId(@Param("tripId") Long tripId);

    // 여러 트립의 지출 총액을 한 번에 조회 (목록 조회 시 N+1 방지)
    @Query("select te.trip.id as tripId, coalesce(sum(te.convertedKrwAmount), 0) as total " +
            "from TripExpense te where te.trip.id in :tripIds group by te.trip.id")
    List<TripExpenseSumProjection> sumConvertedKrwAmountByTripIds(@Param("tripIds") List<Long> tripIds);
}
