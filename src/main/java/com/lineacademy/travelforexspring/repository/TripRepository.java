package com.lineacademy.travelforexspring.repository;

import com.lineacademy.travelforexspring.domain.trip.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {

    // 1. 진행 중 (종료일이 오늘보다 크거나 같은 경우)
    Page<Trip> findAllByUserIdAndEndDateGreaterThanEqualOrderByIdDesc(Long userId, LocalDate today, Pageable pageable);

    // 2. 지난 여행 (종료일이 오늘보다 작은 경우)
    Page<Trip> findAllByUserIdAndEndDateLessThanOrderByIdDesc(Long userId, LocalDate today, Pageable pageable);

    Optional<Trip> findByIdAndUserId(Long id, Long userId);
}
