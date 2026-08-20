package com.lineacademy.travelforexspring.controller;

import com.lineacademy.travelforexspring.domain.notice.Notice;
import com.lineacademy.travelforexspring.dto.common.PaginationResponse;
import com.lineacademy.travelforexspring.dto.notice.response.NoticeResponse;
import com.lineacademy.travelforexspring.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, size);
            Page<Notice> serviceResult = noticeService.getNoticeList(pageRequest);

            List<NoticeResponse> convertList = serviceResult.stream()
                    .map(NoticeResponse::from)
                    .toList();

            PaginationResponse<NoticeResponse> response = PaginationResponse.of(
                    page,
                    size,
                    serviceResult.getTotalElements(),
                    convertList
            );

            return ResponseEntity.ok(Map.of(
                    "message", "공지사항 목록 조회 성공",
                    "data", response
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getNoticeDetail(@PathVariable Long id) {
        try {
            Notice notice = noticeService.getNoticeDetail(id);
            return ResponseEntity.ok(Map.of(
                    "message", "공지사항 상세 조회 성공",
                    "data", NoticeResponse.from(notice)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("NOTICE_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 공지사항을 찾을 수 없습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }
}
