package com.lineacademy.travelforexspring.controller;

import com.lineacademy.travelforexspring.domain.tripexpense.TripExpense;
import com.lineacademy.travelforexspring.dto.common.PaginationResponse;
import com.lineacademy.travelforexspring.dto.tripexpense.request.CreateTripExpenseRequest;
import com.lineacademy.travelforexspring.dto.tripexpense.request.UpdateTripExpenseRequest;
import com.lineacademy.travelforexspring.dto.tripexpense.response.TripExpenseResponse;
import com.lineacademy.travelforexspring.service.TripExpenseService;
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
@RequestMapping("/trips/{tripId}/expenses")
@RequiredArgsConstructor
public class TripExpenseController {

    private final TripExpenseService tripExpenseService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createExpense(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long tripId,
            @Valid @RequestBody CreateTripExpenseRequest request
    ) {
        try {
            TripExpense expense = tripExpenseService.createExpense(currentUserId, tripId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "지출 내역이 성공적으로 등록되었습니다.",
                    "data", TripExpenseResponse.from(expense)
            ));
        } catch (RuntimeException e) {
            return handleExpenseException(e);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getExpenseList(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long tripId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, size);
            Page<TripExpense> serviceResult = tripExpenseService.getExpenseList(currentUserId, tripId, pageRequest);

            List<TripExpenseResponse> convertList = serviceResult.stream()
                    .map(TripExpenseResponse::from)
                    .toList();

            PaginationResponse<TripExpenseResponse> response = PaginationResponse.of(
                    page, size, serviceResult.getTotalElements(), convertList
            );

            return ResponseEntity.ok(Map.of(
                    "message", "지출 내역 목록 조회 성공",
                    "data", response
            ));
        } catch (RuntimeException e) {
            return handleExpenseException(e);
        }
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<Map<String, Object>> getExpenseDetail(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long tripId,
            @PathVariable Long expenseId
    ) {
        try {
            TripExpense expense = tripExpenseService.getExpenseDetail(currentUserId, tripId, expenseId);
            return ResponseEntity.ok(Map.of(
                    "message", "지출 상세 조회 성공",
                    "data", TripExpenseResponse.from(expense)
            ));
        } catch (RuntimeException e) {
            return handleExpenseException(e);
        }
    }

    @PatchMapping("/{expenseId}")
    public ResponseEntity<Map<String, Object>> updateExpense(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long tripId,
            @PathVariable Long expenseId,
            @Valid @RequestBody UpdateTripExpenseRequest request
    ) {
        try {
            TripExpense expense = tripExpenseService.updateExpense(currentUserId, tripId, expenseId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "지출 내역이 성공적으로 수정되었습니다.",
                    "data", TripExpenseResponse.from(expense)
            ));
        } catch (RuntimeException e) {
            return handleExpenseException(e);
        }
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Map<String, Object>> deleteExpense(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long tripId,
            @PathVariable Long expenseId
    ) {
        try {
            tripExpenseService.deleteExpense(currentUserId, tripId, expenseId);
            return ResponseEntity.ok(Map.of("message", "지출 내역이 성공적으로 삭제되었습니다."));
        } catch (RuntimeException e) {
            return handleExpenseException(e);
        }
    }

    // 예외 처리가 중복되므로 헬퍼 메서드 사용
    private ResponseEntity<Map<String, Object>> handleExpenseException(RuntimeException e) {
        if (e.getMessage().equals("TRIP_NOT_FOUND"))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 여행을 찾을 수 없거나 접근 권한이 없습니다."));
        if (e.getMessage().equals("EXPENSE_NOT_FOUND"))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 지출 내역을 찾을 수 없습니다."));
        if (e.getMessage().equals("WALLET_NOT_FOUND") || e.getMessage().equals("WALLET_ID_REQUIRED"))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "유효한 지갑 정보가 필요합니다."));
        if (e.getMessage().equals("CURRENCY_MISMATCH"))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "지출 통화와 지갑의 통화가 일치하지 않습니다."));
        if (e.getMessage().equals("INSUFFICIENT_BALANCE"))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "지갑 잔액이 부족합니다."));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
    }
}
