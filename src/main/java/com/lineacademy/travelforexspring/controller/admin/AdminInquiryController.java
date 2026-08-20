package com.lineacademy.travelforexspring.controller.admin;

import com.lineacademy.travelforexspring.domain.inquiry.Inquiry;
import com.lineacademy.travelforexspring.dto.common.PaginationResponse;
import com.lineacademy.travelforexspring.dto.inquiry.request.AnswerInquiryRequest;
import com.lineacademy.travelforexspring.dto.inquiry.response.InquiryResponse;
import com.lineacademy.travelforexspring.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/inquiries")
@RequiredArgsConstructor
public class AdminInquiryController {

    private final InquiryService inquiryService;

    // 1. 전체 문의 목록 조회
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllInquiries(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, size);
            Page<Inquiry> serviceResult = inquiryService.getAllInquiryList(pageRequest);

            List<InquiryResponse> convertList = serviceResult.stream()
                    .map(InquiryResponse::from)
                    .toList();

            PaginationResponse<InquiryResponse> response = PaginationResponse.of(
                    page,
                    size,
                    serviceResult.getTotalElements(),
                    convertList
            );

            return ResponseEntity.ok(Map.of(
                    "message", "전체 문의 목록 조회 성공",
                    "data", response
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    // 2. 문의 상세 조회 (관리자용)
    @GetMapping("/{inquiryId}")
    public ResponseEntity<Map<String, Object>> getInquiryDetail(@PathVariable Long inquiryId) {
        try {
            Inquiry inquiry = inquiryService.getInquiryDetailForAdmin(inquiryId);
            return ResponseEntity.ok(Map.of(
                    "message", "문의 상세 조회 성공",
                    "data", InquiryResponse.from(inquiry)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("INQUIRY_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 문의를 찾을 수 없습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    // 3. 문의 답변 등록
    @PatchMapping("/{inquiryId}/answer")
    public ResponseEntity<Map<String, Object>> answerInquiry(
            @PathVariable Long inquiryId,
            @Valid @RequestBody AnswerInquiryRequest request
    ) {
        try {
            Inquiry inquiry = inquiryService.answerInquiry(inquiryId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "답변이 성공적으로 등록되었습니다.",
                    "data", InquiryResponse.from(inquiry)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("INQUIRY_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 문의를 찾을 수 없습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }
}
