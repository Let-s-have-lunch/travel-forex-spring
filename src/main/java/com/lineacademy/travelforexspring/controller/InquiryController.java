package com.lineacademy.travelforexspring.controller;

import com.lineacademy.travelforexspring.domain.inquiry.Inquiry;
import com.lineacademy.travelforexspring.dto.common.PaginationResponse;
import com.lineacademy.travelforexspring.dto.inquiry.request.CreateInquiryRequest;
import com.lineacademy.travelforexspring.dto.inquiry.request.UpdateInquiryRequest;
import com.lineacademy.travelforexspring.dto.inquiry.response.InquiryResponse;
import com.lineacademy.travelforexspring.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createInquiry(
            @AuthenticationPrincipal Long currentUserId,
            @Valid @RequestBody CreateInquiryRequest request
    ) {
        try {
            Inquiry inquiry = inquiryService.createInquiry(currentUserId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "문의가 성공적으로 등록되었습니다.",
                            "data", InquiryResponse.from(inquiry)
                    ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("USER_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "사용자 정보를 찾을 수 없습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyInquiries(
            @AuthenticationPrincipal Long currentUserId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, size);
            Page<Inquiry> serviceResult = inquiryService.getMyInquiryList(currentUserId, pageRequest);

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
                    "message", "문의 목록 조회 성공",
                    "data", response
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @GetMapping("/{inquiryId}")
    public ResponseEntity<Map<String, Object>> getInquiryDetail(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long inquiryId
    ) {
        try {
            Inquiry inquiry = inquiryService.getMyInquiryDetail(currentUserId, inquiryId);
            return ResponseEntity.ok(Map.of(
                    "message", "문의 상세 조회 성공",
                    "data", InquiryResponse.from(inquiry)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("INQUIRY_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 문의를 찾을 수 없거나 접근 권한이 없습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @PatchMapping("/{inquiryId}")
    public ResponseEntity<Map<String, Object>> updateInquiry(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long inquiryId,
            @Valid @RequestBody UpdateInquiryRequest request
    ) {
        try {
            Inquiry inquiry = inquiryService.updateInquiry(currentUserId, inquiryId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "문의 내용이 성공적으로 수정되었습니다.",
                    "data", InquiryResponse.from(inquiry)
            ));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("INQUIRY_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 문의를 찾을 수 없거나 접근 권한이 없습니다."));
            if (e.getMessage().equals("CANNOT_UPDATE_ANSWERED_INQUIRY"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "답변이 완료된 문의는 수정할 수 없습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }

    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<Map<String, Object>> deleteInquiry(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long inquiryId
    ) {
        try {
            inquiryService.deleteInquiry(currentUserId, inquiryId);
            return ResponseEntity.ok(Map.of("message", "문의가 성공적으로 삭제되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("INQUIRY_NOT_FOUND"))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 문의를 찾을 수 없거나 접근 권한이 없습니다."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 에러"));
        }
    }
}
