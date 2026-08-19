package com.lineacademy.travelforexspring.controller;

import com.lineacademy.travelforexspring.domain.wallet.Wallet;
import com.lineacademy.travelforexspring.dto.wallet.request.CreateWalletRequest;
import com.lineacademy.travelforexspring.dto.wallet.response.WalletResponse;
import com.lineacademy.travelforexspring.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createWallet(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody CreateWalletRequest request
    ) {
        try {
            Wallet newWallet = walletService.createWallet(currentUserId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "지갑이 성공적으로 생성되었습니다.",
                            "data", WalletResponse.from(newWallet)
                    ));
        } catch (RuntimeException e) {
            // 수정포인트 1: NullPointerException 방지를 위해 문자열을 앞에 두는 방식(Null-safe)으로 변경
            if ("USER_NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "사용자 정보를 찾을 수 없습니다."));
            }
            if ("ALREADY_EXISTS_WALLET".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "이미 해당 통화의 지갑이 존재합니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyWallets(
            @AuthenticationPrincipal Long currentUserId
    ) {
        List<Wallet> wallets = walletService.getMyWallets(currentUserId);

        List<WalletResponse> walletResponses = wallets.stream()
                .map(WalletResponse::from)
                .toList();

        return ResponseEntity.ok(Map.of(
                "message", "지갑 목록 조회 성공",
                "data", walletResponses
        ));
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<Map<String, Object>> getWalletDetail(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable("walletId") Long walletId // 수정포인트 2: 최신 스프링 버전을 위해 파라미터 이름("walletId") 명시
    ) {
        try {
            Wallet wallet = walletService.getWalletDetail(currentUserId, walletId);
            return ResponseEntity.ok(Map.of(
                    "message", "지갑 상세 조회 성공",
                    "data", WalletResponse.from(wallet)
            ));
        } catch (RuntimeException e) {
            if ("WALLET_NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 지갑을 찾을 수 없거나 접근 권한이 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @DeleteMapping("/{walletId}")
    public ResponseEntity<Map<String, Object>> deleteWallet(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable("walletId") Long walletId // 수정포인트 2: 최신 스프링 버전을 위해 파라미터 이름("walletId") 명시
    ) {
        try {
            walletService.deleteWallet(currentUserId, walletId);
            return ResponseEntity.ok(Map.of("message", "지갑이 성공적으로 삭제되었습니다."));
        } catch (RuntimeException e) {
            if ("WALLET_NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 지갑을 찾을 수 없거나 접근 권한이 없습니다."));
            }
            if ("BALANCE_NOT_ZERO".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "잔액이 남아있는 지갑은 삭제할 수 없습니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }
}