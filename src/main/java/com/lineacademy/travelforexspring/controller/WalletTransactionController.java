package com.lineacademy.travelforexspring.controller;

import com.lineacademy.travelforexspring.domain.wallettransaction.WalletTransaction;
import com.lineacademy.travelforexspring.dto.common.CursorPaginationResponse;
import com.lineacademy.travelforexspring.dto.wallettransaction.request.CreateTransactionRequest;
import com.lineacademy.travelforexspring.dto.wallettransaction.request.UpdateTransactionRequest;
import com.lineacademy.travelforexspring.dto.wallettransaction.response.TransactionResponse;
import com.lineacademy.travelforexspring.service.WalletTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wallets/{walletId}/transactions")
@RequiredArgsConstructor
public class WalletTransactionController {

    private final WalletTransactionService walletTransactionService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTransaction(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable("walletId") Long walletId,
            @Valid @RequestBody CreateTransactionRequest request
    ) {
        try {
            WalletTransaction newTransaction = walletTransactionService.createTransaction(currentUserId, walletId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "거래 내역이 성공적으로 등록되었습니다.",
                            "data", TransactionResponse.from(newTransaction)
                    ));
        } catch (RuntimeException e) {
            if ("WALLET_NOT_FOUND".equals(e.getMessage()))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 지갑을 찾을 수 없거나 접근 권한이 없습니다."));
            if ("INSUFFICIENT_BALANCE".equals(e.getMessage()))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "지갑 잔액이 부족합니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getTransactions(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable("walletId") Long walletId,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            List<WalletTransaction> transactions = walletTransactionService.getTransactionList(currentUserId, walletId, cursorId, size + 1);

            boolean hasNext = false;
            if (transactions.size() > size) {
                hasNext = true;
                transactions.remove(size);
            }

            List<TransactionResponse> convertList = transactions.stream()
                    .map(TransactionResponse::from)
                    .collect(Collectors.toList());

            Long nextCursorId = convertList.isEmpty() ? null : convertList.get(convertList.size() - 1).getId();

            CursorPaginationResponse<TransactionResponse> response = CursorPaginationResponse.of(
                    convertList,
                    hasNext,
                    nextCursorId
            );

            return ResponseEntity.ok(Map.of(
                    "message", "거래 내역 조회 성공",
                    "data", response
            ));
        } catch (RuntimeException e) {
            if ("WALLET_NOT_FOUND".equals(e.getMessage()))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 지갑을 찾을 수 없거나 접근 권한이 없습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Map<String, Object>> getTransaction(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long walletId,
            @PathVariable Long transactionId
    ) {
        try {
            WalletTransaction transaction = walletTransactionService.getTransaction(currentUserId, walletId, transactionId);

            return ResponseEntity.ok(Map.of(
                    "message", "거래 내역 상세 조회 성공",
                    "data", TransactionResponse.from(transaction)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("WALLET_NOT_FOUND") || e.getMessage().equals("TRANSACTION_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 내역을 찾을 수 없거나 접근 권한이 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @PatchMapping("/{transactionId}")
    public ResponseEntity<Map<String, Object>> updateTransaction(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long walletId,
            @PathVariable Long transactionId,
            @Valid @RequestBody UpdateTransactionRequest request
    ) {
        try {
            WalletTransaction updatedTransaction = walletTransactionService.updateTransaction(currentUserId, walletId, transactionId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "거래 내역이 성공적으로 수정되었습니다.",
                    "data", TransactionResponse.from(updatedTransaction)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("WALLET_NOT_FOUND") || e.getMessage().equals("TRANSACTION_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 내역을 찾을 수 없거나 접근 권한이 없습니다."));
            if (e.getMessage().equals("INSUFFICIENT_BALANCE"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "수정 시 지갑 잔액이 마이너스가 되어 처리할 수 없습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Map<String, Object>> deleteTransaction(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long walletId,
            @PathVariable Long transactionId
    ) {
        try {
            walletTransactionService.deleteTransaction(currentUserId, walletId, transactionId);
            return ResponseEntity.ok(Map.of(
                    "message", "거래 내역이 성공적으로 삭제되었습니다."
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("WALLET_NOT_FOUND") || e.getMessage().equals("TRANSACTION_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 내역을 찾을 수 없거나 접근 권한이 없습니다."));
            if (e.getMessage().equals("INSUFFICIENT_BALANCE"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "삭제 시 지갑 잔액이 마이너스가 되어 처리할 수 없습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }
}