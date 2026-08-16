package com.lineacademy.travelforexspring.controller;

import com.lineacademy.travelforexspring.domain.trip.Trip;
import com.lineacademy.travelforexspring.dto.common.PaginationResponse;
import com.lineacademy.travelforexspring.dto.trip.request.CreateTripRequest;
import com.lineacademy.travelforexspring.dto.trip.request.UpdateTripRequest;
import com.lineacademy.travelforexspring.dto.trip.response.TripResponse;
import com.lineacademy.travelforexspring.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTrip(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody CreateTripRequest request
    ) {
        try {
            Trip newTrip = tripService.createTrip(currentUserId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "여행 일정이 성공적으로 등록되었습니다.",
                            "data", TripResponse.from(newTrip)
                    ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("USER_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "사용자 정보를 찾을 수 없습니다."));
            if (e.getMessage().equals("INVALID_DATE_RANGE"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "시작일은 종료일보다 이전이어야 합니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getTripList(
            @AuthenticationPrincipal Long currentUserId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, size);
            Page<Trip> serviceResult = tripService.getTripList(currentUserId, pageRequest);

            List<TripResponse> convertList = serviceResult.stream()
                    .map(TripResponse::from)
                    .toList();

            PaginationResponse<TripResponse> response = PaginationResponse.of(
                    page,
                    size,
                    serviceResult.getTotalElements(),
                    convertList
            );

            return ResponseEntity.ok(Map.of(
                    "message", "여행 목록 조회 성공",
                    "data", response
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<Map<String, Object>> getTripDetail(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long tripId
    ) {
        try {
            Trip trip = tripService.getTripDetail(currentUserId, tripId);
            return ResponseEntity.ok(Map.of(
                    "message", "여행 상세 조회 성공",
                    "data", TripResponse.from(trip)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("TRIP_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 여행 일정을 찾을 수 없거나 접근 권한이 없습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @PatchMapping("/{tripId}")
    public ResponseEntity<Map<String, Object>> updateTrip(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long tripId,
            @Valid @RequestBody UpdateTripRequest request
    ) {
        try {
            Trip updatedTrip = tripService.updateTrip(currentUserId, tripId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "여행 일정이 성공적으로 수정되었습니다.",
                    "data", TripResponse.from(updatedTrip)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("TRIP_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 여행 일정을 찾을 수 없거나 접근 권한이 없습니다."));
            if (e.getMessage().equals("INVALID_DATE_RANGE"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "시작일은 종료일보다 이전이어야 합니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<Map<String, Object>> deleteTrip(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long tripId
    ) {
        try {
            tripService.deleteTrip(currentUserId, tripId);
            return ResponseEntity.ok(Map.of("message", "여행 일정이 성공적으로 삭제되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("TRIP_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 여행 일정을 찾을 수 없거나 접근 권한이 없습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }
}