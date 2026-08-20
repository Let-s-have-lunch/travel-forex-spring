package com.lineacademy.travelforexspring.service;

import com.lineacademy.travelforexspring.domain.enums.InquiryStatus;
import com.lineacademy.travelforexspring.domain.inquiry.Inquiry;
import com.lineacademy.travelforexspring.domain.user.User;
import com.lineacademy.travelforexspring.dto.inquiry.request.AnswerInquiryRequest;
import com.lineacademy.travelforexspring.dto.inquiry.request.CreateInquiryRequest;
import com.lineacademy.travelforexspring.dto.inquiry.request.UpdateInquiryRequest;
import com.lineacademy.travelforexspring.repository.InquiryRepository;
import com.lineacademy.travelforexspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    // ==========================================
    // 사용자용 (User)
    // ==========================================

    @Transactional
    public Inquiry createInquiry(Long userId, CreateInquiryRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        Inquiry inquiry = Inquiry.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        return inquiryRepository.save(inquiry);
    }

    @Transactional(readOnly = true)
    public Page<Inquiry> getMyInquiryList(Long userId, Pageable pageable) {
        return inquiryRepository.findAllByUserIdOrderByIdDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Inquiry getMyInquiryDetail(Long userId, Long inquiryId) {
        return inquiryRepository.findByIdAndUserId(inquiryId, userId)
                .orElseThrow(() -> new RuntimeException("INQUIRY_NOT_FOUND"));
    }

    @Transactional
    public Inquiry updateInquiry(Long userId, Long inquiryId, UpdateInquiryRequest request) {
        Inquiry inquiry = inquiryRepository.findByIdAndUserId(inquiryId, userId)
                .orElseThrow(() -> new RuntimeException("INQUIRY_NOT_FOUND"));

        // 답변이 완료된 문의는 수정 불가
        if (inquiry.getStatus() == InquiryStatus.ANSWERED) {
            throw new RuntimeException("CANNOT_UPDATE_ANSWERED_INQUIRY");
        }

        inquiry.updateInquiry(request.getTitle(), request.getContent());
        return inquiry;
    }

    @Transactional
    public void deleteInquiry(Long userId, Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findByIdAndUserId(inquiryId, userId)
                .orElseThrow(() -> new RuntimeException("INQUIRY_NOT_FOUND"));

        inquiry.softDeleteData();
    }

    // ==========================================
    // 관리자용 (Admin)
    // ==========================================

    @Transactional(readOnly = true)
    public Page<Inquiry> getAllInquiryList(Pageable pageable) {
        return inquiryRepository.findAllByOrderByIdDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Inquiry getInquiryDetailForAdmin(Long inquiryId) {
        return inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("INQUIRY_NOT_FOUND"));
    }

    @Transactional
    public Inquiry answerInquiry(Long inquiryId, AnswerInquiryRequest request) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("INQUIRY_NOT_FOUND"));

        // 엔티티에 미리 만들어둔 편의 메서드 사용
        inquiry.addAnswer(request.getAnswer());

        return inquiry;
    }

    @Transactional
    public void deleteInquiryAnswer(Long inquiryId) {
        Inquiry inquiry = getInquiryDetailForAdmin(inquiryId);

        inquiry.deleteAnswer();
    }
}
