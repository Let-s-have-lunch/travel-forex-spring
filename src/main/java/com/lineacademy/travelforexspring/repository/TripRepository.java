package com.lineacademy.travelforexspring.repository;

import com.lineacademy.travelforexspring.domain.trip.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {

    // Spring Data JPA는 파라미터로 Pageable이 전달되면 페이징 쿼리로 인식합니다.
    // 데이터 조회(LIMIT/OFFSET)와 전체 개수 조회(COUNT)를 함께 수행하여 페이징 메타데이터가 포함된 Page<Trip> 객체로 반환합니다.
    Page<Trip> findAllByUserIdOrderByIdDesc(Long userId, Pageable pageable);

    Optional<Trip> findByIdAndUserId(Long id, Long userId);
}
