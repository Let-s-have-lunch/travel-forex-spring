package com.lineacademy.travelforexspring.repository;

import com.lineacademy.travelforexspring.domain.trip.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {

    Page<Trip> findAllByUserIdOrderByIdDesc(Long userId, Pageable pageable);

    Optional<Trip> findByIdAndUserId(Long id, Long userId);
}
