package com.lineacademy.travelforexspring.controller;

import com.lineacademy.travelforexspring.domain.user.User;
import com.lineacademy.travelforexspring.dto.user.request.*;
import com.lineacademy.travelforexspring.dto.user.response.UserResponse;
import com.lineacademy.travelforexspring.service.UserService;
import com.lineacademy.travelforexspring.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            User newUser = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "성공적으로 회원가입 되었습니다.",
                            "data", UserResponse.from(newUser)
                    ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("ALREADY_EXISTS_EMAIL"))
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "이미 가입된 이메일입니다."));
            if (e.getMessage().equals("ALREADY_EXISTS_NICKNAME"))
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "이미 사용 중인 닉네임입니다."));
            if (e.getMessage().equals("ALREADY_EXISTS_PHONE"))
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "이미 사용 중인 전화번호입니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userService.login(request);
            String token = jwtUtil.generateToken(user.getId());

            return ResponseEntity.ok(
                    Map.of(
                            "message", "로그인에 성공했습니다.",
                            "data", Map.of(
                                    "user", UserResponse.from(user),
                                    "token", token)
                    )
            );
        } catch (RuntimeException e) {
            if (e.getMessage().equals("INVALID_CREDENTIAL"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "아이디 또는 비밀번호가 일치하지 않습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe(@AuthenticationPrincipal Long currentUserId) {
        try {
            User user = userService.getUserById(currentUserId);
            return ResponseEntity.ok(Map.of(
                    "message", "사용자 정보 확인이 완료되었습니다.",
                    "data", UserResponse.from(user)
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "유효하지 않은 사용자이거나 탈퇴한 계정입니다."));
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUser(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        try {
            User updatedUser = userService.updateUser(currentUserId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "회원 정보가 성공적으로 수정되었습니다.",
                    "data", UserResponse.from(updatedUser)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("USER_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 사용자를 찾을 수 없습니다."));
            if (e.getMessage().equals("ALREADY_EXISTS_NICKNAME"))
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "이미 사용 중인 닉네임입니다."));
            if (e.getMessage().equals("ALREADY_EXISTS_PHONE"))
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "이미 사용 중인 전화번호입니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @PatchMapping("/password")
    public ResponseEntity<Map<String, Object>> updatePassword(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        try {
            userService.updatePassword(currentUserId, request);
            return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("USER_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 사용자를 찾을 수 없습니다."));
            if (e.getMessage().equals("INVALID_PASSWORD"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "현재 비밀번호가 일치하지 않습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @PostMapping("/password-reset")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @RequestBody ResetPasswordRequest request
    ) {
        try {
            userService.resetPassword(request.getEmail(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("success", true, "message", "비밀번호가 성공적으로 변경되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @PatchMapping("/withdraw")
    public ResponseEntity<Map<String, Object>> withdrawUser(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody WithdrawUserRequest request
    ) {
        try {
            userService.withdrawUser(currentUserId, request);
            return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 성공적으로 처리되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("USER_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 사용자를 찾을 수 없습니다."));
            if (e.getMessage().equals("INVALID_CREDENTIAL"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "현재 비밀번호가 일치하지 않습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }
}
