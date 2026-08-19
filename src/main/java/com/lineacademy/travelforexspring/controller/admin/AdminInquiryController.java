package com.lineacademy.travelforexspring.controller.admin;

import com.lineacademy.travelforexspring.domain.inquiry.Inquiry;
import com.lineacademy.travelforexspring.dto.admin.inquiry.request.InquiryAnswerRequest;
import com.lineacademy.travelforexspring.dto.common.PaginationResponse;
import com.lineacademy.travelforexspring.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/inquiries")
@RequiredArgsConstructor
public class AdminInquiryController {

    private final InquiryService inquiryService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getInquiryList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<Inquiry> inquiryPage = inquiryService.getAllInquiryList(page, size);

            List<InquiryResponse> list = inquiryPage.getContent().stream()
                    .map(InquiryResponse::from)
                    .toList();

            PaginationResponse<InquiryResponse> paginationData = PaginationResponse.of(
                    page,
                    size,
                    inquiryPage.getTotalElements(),
                    list
            );

            return ResponseEntity.ok(Map.<String, Object>of(
                    "message", "문의 목록을 성공적으로 조회했습니다.",
                    "data", paginationData
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.<String, Object>of("message", "문의 목록 조회 중 서버 오류가 발생되었습니다."));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{inquiryId}")
    public ResponseEntity<Map<String, Object>> getInquiryById(@PathVariable Long inquiryId) {
        try {
            Inquiry inquiry = inquiryService.getInquiryById(inquiryId);
            return ResponseEntity.ok(Map.<String, Object>of(
                    "message", "문의 글을 성공적으로 조회했습니다.",
                    "data", InquiryResponse.from(inquiry)
            ));
        } catch (RuntimeException e) {
            if ("NOT_FOUND_INQUIRY".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("message", "존재하지 않거나 삭제된 문의글 입니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.<String, Object>of("message", "문의글 조회 중 서버 오류가 발생되었습니다."));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{inquiryId}")
    public ResponseEntity<Map<String, Object>> answerInquiry(
            @PathVariable Long inquiryId,
            @Valid @RequestBody InquiryAnswerRequest request) {
        try {
            Inquiry result = inquiryService.answerInquiry(inquiryId, request);
            return ResponseEntity.ok(Map.<String, Object>of(
                    "message", "문의 답변 작업 성공",
                    "data", InquiryResponse.from(result)
            ));
        } catch (RuntimeException e) {
            if ("NOT_FOUND_INQUIRY".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("message", "존재하지 않거나 삭제된 문의글 입니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.<String, Object>of("message", "문의글 작업 중 서버 오류가 발생되었습니다."));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<Map<String, Object>> deleteInquiryAnswer(@PathVariable Long inquiryId) {
        try {
            inquiryService.deleteInquiryAnswer(inquiryId);
            return ResponseEntity.ok(Map.<String, Object>of(
                    "message", "문의 답변 삭제 작업 성공"
            ));
        } catch (RuntimeException e) {
            if ("NOT_FOUND_INQUIRY".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.<String, Object>of("message", "존재하지 않거나 삭제된 문의글 입니다."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.<String, Object>of("message", "문의글 답변 삭제 중 서버 오류가 발생되었습니다."));
        }
    }
}
