package com.lineacademy.travelforexspring.controller.admin;

import com.lineacademy.travelforexspring.domain.notice.Notice;
import com.lineacademy.travelforexspring.dto.common.PaginationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/notice")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final NoticeService noticeService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getNoticeList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        try {
            Page<Notice> noticePage = noticeService.getNoticeList(page, size);

            List<NoticeResponse> list = noticePage.getContent().stream()
                    .map(NoticeResponse::from)
                    .toList();

            PaginationResponse<NoticeResponse> paginationData = PaginationResponse.of(
                    page,
                    size,
                    noticePage.getTotalElements(),
                    list
            );

            return ResponseEntity.ok(Map.of(
                    "message", "공지사항 목록 조회 성공",
                    "data", paginationData
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "공지사항 목록 조회 중 서버 에러가 발생되었습니다."));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{noticeId}")
    public ResponseEntity<Map<String, Object>> getNoticeById(@PathVariable Long noticeId) {
        try {
            Notice notice = noticeService.getNoticeById(noticeId);
            return ResponseEntity.ok(Map.of(
                    "message", "공지사항 상세 조회 성공",
                    "data", NoticeResponse.from(notice)
            ));
        } catch (RuntimeException e) {
            if ("NOT_FOUND_NOTICE".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "존재하지 않는 공지사항입니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "공지사항 상세 조회 중 서버 에러가 발생되었습니다."));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createNotice(@Valid @RequestBody NoticeRequest request) {
        try {
            Notice result = noticeService.createNotice(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "공지사항이 정상적으로 등록되었습니다.",
                    "data", NoticeResponse.from(result)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "공지사항 등록 중 서버 에러가 발생되었습니다."));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{noticeId}")
    public ResponseEntity<Map<String, Object>> updateNotice(
            @PathVariable Long noticeId,
            @Valid @RequestBody NoticeRequest request) {
        try {
            Notice result = noticeService.updateNotice(noticeId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "공지사항이 수정되었습니다.",
                    "data", NoticeResponse.from(result)
            ));
        } catch (RuntimeException e) {
            if ("NOT_FOUND_NOTICE".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "존재하지 않는 공지사항입니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "공지사항 수정 중 서버 에러가 발생되었습니다."));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Map<String, Object>> deleteNotice(@PathVariable Long noticeId) {
        try {
            noticeService.deleteNotice(noticeId);
            return ResponseEntity.ok(Map.of(
                    "message", "공지사항이 삭제되었습니다."
            ));
        } catch (RuntimeException e) {
            if ("NOT_FOUND_NOTICE".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "존재하지 않는 공지사항입니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "공지사항 삭제 중 서버 에러가 발생되었습니다."));
        }
    }
}
