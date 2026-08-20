package com.lineacademy.travelforexspring.controller.admin;

import com.lineacademy.travelforexspring.domain.notice.Notice;
import com.lineacademy.travelforexspring.dto.notice.request.CreateNoticeRequest;
import com.lineacademy.travelforexspring.dto.notice.request.UpdateNoticeRequest;
import com.lineacademy.travelforexspring.dto.notice.response.NoticeResponse;
import com.lineacademy.travelforexspring.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/notices")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminNoticeController {
    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createNotice(@Valid @RequestBody CreateNoticeRequest request) {
        try {
            Notice notice = noticeService.createNotice(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "공지사항이 성공적으로 작성되었습니다.",
                            "data", NoticeResponse.from(notice)
                    ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateNotice(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNoticeRequest request
    ) {
        try {
            Notice notice = noticeService.updateNotice(id, request);
            return ResponseEntity.ok(Map.of(
                    "message", "공지사항이 성공적으로 수정되었습니다.",
                    "data", NoticeResponse.from(notice)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NOTICE_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 공지사항을 찾을 수 없습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteNotice(@PathVariable Long id) {
        try {
            noticeService.deleteNotice(id);
            return ResponseEntity.ok(Map.of("message", "공지사항이 성공적으로 삭제되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NOTICE_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 공지사항을 찾을 수 없습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }
}
