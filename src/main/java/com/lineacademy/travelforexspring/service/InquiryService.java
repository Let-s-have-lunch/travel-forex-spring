package com.lineacademy.travelforexspring.service;

import com.lineacademy.travelforexspring.domain.inquiry.Inquiry;
import com.lineacademy.travelforexspring.domain.user.User;
import com.lineacademy.travelforexspring.dto.admin.inquiry.request.InquiryAnswerRequest;
import com.lineacademy.travelforexspring.repository.InquiryRepository;
import com.lineacademy.travelforexspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    // ==========================================
    // 일반 유저 전용 서비스
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
    public Page<Inquiry> getMyInquiryList(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return inquiryRepository.findAllByUserIdOrderByIdDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Inquiry getInquiryById(Long inquiryId) {
        return inquiryRepository.findByIdWithUser(inquiryId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND_INQUIRY"));
    }

    @Transactional
    public void deleteInquiry(Long userId, Long inquiryId) {
        Inquiry inquiry = getInquiryById(inquiryId);

        if (!inquiry.getUser().getId().equals(userId)) {
            throw new RuntimeException("UNAUTHORIZED_ACCESS");
        }

        inquiryRepository.delete(inquiry);
    }

    // ==========================================
    // 어드민 전용 서비스
    // ==========================================

    @Transactional(readOnly = true)
    public Page<Inquiry> getAllInquiryList(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return inquiryRepository.findAllWithUserByOrderByIdDesc(pageable);
    }

    @Transactional
    public Inquiry answerInquiry(Long inquiryId, InquiryAnswerRequest request) {
        Inquiry inquiry = getInquiryById(inquiryId);

        // 엔티티의 addAnswer() 호출
        inquiry.addAnswer(request.getAnswer());

        return inquiry;
    }

    @Transactional
    public void deleteInquiryAnswer(Long inquiryId) {
        Inquiry inquiry = getInquiryById(inquiryId);

        // 엔티티의 deleteAnswer() 호출
        inquiry.deleteAnswer();
    }
}