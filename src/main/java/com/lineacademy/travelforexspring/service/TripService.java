package com.lineacademy.travelforexspring.service;

import com.lineacademy.travelforexspring.domain.trip.Trip;
import com.lineacademy.travelforexspring.domain.user.User;
import com.lineacademy.travelforexspring.dto.trip.request.CreateTripRequest;
import com.lineacademy.travelforexspring.dto.trip.request.UpdateTripRequest;
import com.lineacademy.travelforexspring.repository.TripRepository;
import com.lineacademy.travelforexspring.repository.UserRepository;
import com.lineacademy.travelforexspring.utils.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    @Transactional
    public Trip createTrip(Long userId, CreateTripRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        DateUtil.validateDateRange(request.getStartDate(), request.getEndDate());

        Trip trip = Trip.builder()
                .user(user)
                .title(request.getTitle())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .budgetKrw(request.getBudgetKrw())
                .build();

        return tripRepository.save(trip);
    }

    @Transactional(readOnly = true)
    public Page<Trip> getTripList(Long userId, Pageable pageable) {
        return tripRepository.findAllByUserIdOrderByIdDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Trip getTripDetail(Long userId, Long tripId) {
        return tripRepository.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new RuntimeException("TRIP_NOT_FOUND"));
    }

    @Transactional
    public Trip updateTrip(Long userId, Long tripId, UpdateTripRequest request) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new RuntimeException("TRIP_NOT_FOUND"));

        DateUtil.validateDateRange(request.getStartDate(), request.getEndDate());

        trip.updateTrip(
                request.getTitle(),
                request.getStartDate(),
                request.getEndDate(),
                request.getBudgetKrw()
        );

        return trip;
    }

    @Transactional
    public void deleteTrip(Long userId, Long tripId) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new RuntimeException("TRIP_NOT_FOUND"));

        trip.softDeleteData();
    }
}
