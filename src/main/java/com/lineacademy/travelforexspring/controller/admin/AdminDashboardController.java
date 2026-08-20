package com.lineacademy.travelforexspring.controller.admin;

import com.lineacademy.travelforexspring.dto.admin.response.AdminDashboardResponse;
import com.lineacademy.travelforexspring.service.admin.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    // 💡 Express의 requiredAdmin 미들웨어를 이 어노테이션 하나로 완벽히 대체합니다!
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        try {
            AdminDashboardResponse result = adminDashboardService.getDashboardSummary();

            return ResponseEntity.ok(Map.<String, Object>of(
                    "message", "관리자 대시보드 정보를 성공적으로 조회했습니다.",
                    "data", result
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.<String, Object>of("message", "관리자 대시보드 조회 중 서버 에러가 발생했습니다."));
        }
    }
}
