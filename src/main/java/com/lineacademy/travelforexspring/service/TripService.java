package com.lineacademy.travelforexspring.service;

import com.lineacademy.travelforexspring.domain.trip.Trip;
import com.lineacademy.travelforexspring.domain.user.User;
import com.lineacademy.travelforexspring.dto.general.trip.request.CreateTripRequest;
import com.lineacademy.travelforexspring.dto.general.trip.request.UpdateTripRequest;
import com.lineacademy.travelforexspring.dto.general.trip.response.TripResponse;
import com.lineacademy.travelforexspring.repository.TripExpenseRepository;
import com.lineacademy.travelforexspring.repository.TripExpenseSumProjection;
import com.lineacademy.travelforexspring.repository.TripRepository;
import com.lineacademy.travelforexspring.repository.UserRepository;
import com.lineacademy.travelforexspring.utils.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripExpenseRepository tripExpenseRepository;

    @Transactional
    public TripResponse createTrip(Long userId, CreateTripRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        DateUtil.validateDateRange(request.getStartDate(), request.getEndDate());

        Trip trip = Trip.builder()
                .user(user)
                .title(request.getTitle())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .budgetKrw(request.getBudgetKrw())
                .currency(request.getCurrency())
                .build();

        Trip savedTrip = tripRepository.save(trip);

        // 신규 트립은 지출이 없으므로 총액은 항상 0
        return TripResponse.from(savedTrip, BigDecimal.ZERO);
    }

    @Transactional(readOnly = true)
    public Page<TripResponse> getTripList(Long userId, String status, Pageable pageable) {
        LocalDate today = LocalDate.now();

        Page<Trip> tripPage = "PAST".equals(status)
                ? tripRepository.findAllByUserIdAndEndDateLessThanOrderByIdDesc(userId, today, pageable)
                : tripRepository.findAllByUserIdAndEndDateGreaterThanEqualOrderByIdDesc(userId, today, pageable);

        List<Long> tripIds = tripPage.getContent().stream()
                .map(Trip::getId)
                .toList();

        Map<Long, BigDecimal> totalExpenseByTripId = tripIds.isEmpty()
                ? Map.of()
                : tripExpenseRepository.sumConvertedKrwAmountByTripIds(tripIds).stream()
                .collect(java.util.stream.Collectors.toMap(
                        TripExpenseSumProjection::getTripId,
                        TripExpenseSumProjection::getTotal
                ));

        return tripPage.map(trip ->
                TripResponse.from(trip, totalExpenseByTripId.getOrDefault(trip.getId(), BigDecimal.ZERO))
        );
    }

    @Transactional(readOnly = true)
    public TripResponse getTripDetail(Long userId, Long tripId) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new RuntimeException("TRIP_NOT_FOUND"));

        BigDecimal total = tripExpenseRepository.sumConvertedKrwAmountByTripId(tripId);
        return TripResponse.from(trip, total);
    }

    @Transactional
    public TripResponse updateTrip(Long userId, Long tripId, UpdateTripRequest request) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new RuntimeException("TRIP_NOT_FOUND"));

        DateUtil.validateDateRange(request.getStartDate(), request.getEndDate());

        trip.updateTrip(
                request.getTitle(),
                request.getStartDate(),
                request.getEndDate(),
                request.getBudgetKrw(),
                request.getCurrency()
        );

        BigDecimal total = tripExpenseRepository.sumConvertedKrwAmountByTripId(tripId);
        return TripResponse.from(trip, total);
    }

    @Transactional
    public void deleteTrip(Long userId, Long tripId) {
        Trip trip = tripRepository.findByIdAndUserId(tripId, userId)
                .orElseThrow(() -> new RuntimeException("TRIP_NOT_FOUND"));

        trip.softDeleteData();
    }
}