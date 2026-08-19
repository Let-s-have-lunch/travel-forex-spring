package com.lineacademy.travelforexspring.controller.admin;

import com.lineacademy.travelforexspring.domain.user.User;
import com.lineacademy.travelforexspring.dto.admin.user.request.AdminUserUpdateRequest;
import com.lineacademy.travelforexspring.dto.common.PaginationResponse;
import com.lineacademy.travelforexspring.dto.user.response.UserResponse;
import com.lineacademy.travelforexspring.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<User> userPage = userService.getUserList(page, size);

            List<UserResponse> list = userPage.getContent().stream()
                    .map(UserResponse::from)
                    .toList();

            PaginationResponse<UserResponse> paginationData = PaginationResponse.of(
                    page,
                    size,
                    userPage.getTotalElements(),
                    list
            );

            return ResponseEntity.ok(Map.of(
                    "message", "유저 목록을 성공적으로 불러왔습니다.",
                    "data", paginationData
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "유저 목록을 불러오는 중 오류가 발생했습니다."));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(Map.of(
                    "message", "유저 정보를 성공적으로 불러왔습니다.",
                    "data", UserResponse.from(user)
            ));
        } catch (RuntimeException e) {
            if ("USER_NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "존재하지 않는 유저입니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버에 에러가 발생했습니다."));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserUpdateRequest request) {
        try {
            User result = userService.adminUpdateUser(id, request);
            return ResponseEntity.ok(Map.of(
                    "message", "유저를 성공적으로 변경했습니다.",
                    "data", UserResponse.from(result)
            ));
        } catch (RuntimeException e) {
            switch (e.getMessage()) {
                case "USER_NOT_FOUND":
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("message", "사용자를 찾을 수 없습니다."));
                case "ALREADY_EXISTS_EMAIL":
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(Map.of("message", "이미 사용 중인 이메일입니다."));
                case "ALREADY_EXISTS_NICKNAME":
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(Map.of("message", "이미 사용 중인 닉네임입니다."));
                case "ALREADY_EXISTS_PHONE":
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(Map.of("message", "이미 사용 중인 전화번호입니다."));
                default:
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("message", "서버 에러가 발생했습니다."));
            }
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/delete")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        try {
            User deletedUser = userService.adminDeleteUser(id);
            return ResponseEntity.ok(Map.of(
                    "message", "유저가 성공적으로 삭제되었습니다.",
                    "data", UserResponse.from(deletedUser)
            ));
        } catch (RuntimeException e) {
            if ("USER_NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "유저를 찾을 수 없습니다."));
            }
            if ("USER_ALREADY_DELETED".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "이미 삭제된 유저입니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 에러가 발생했습니다."));
        }
    }
}
