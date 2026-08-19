package com.lineacademy.travelforexspring.repository;


import com.lineacademy.travelforexspring.domain.tripexpense.TripExpense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TripExpenseRepository extends JpaRepository<TripExpense, Long> {
    // 특정 여행의 지출 내역을 최신 지출일 순으로 정렬하여 조회
    Page<TripExpense> findAllByTripIdOrderByExpenseDateDesc(Long tripId, Pageable pageable);

    Optional<TripExpense> findByIdAndTripId(Long id, Long tripId);
}
