package com.lineacademy.travelforexspring.service;

import com.lineacademy.travelforexspring.domain.notice.Notice;
import com.lineacademy.travelforexspring.dto.notice.request.CreateNoticeRequest;
import com.lineacademy.travelforexspring.dto.notice.request.UpdateNoticeRequest;
import com.lineacademy.travelforexspring.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;

    @Transactional(readOnly = true)
    public Page<Notice> getNoticeList(Pageable pageable) {
        return noticeRepository.findAllByOrderByIdDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Notice getNoticeDetail(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOTICE_NOT_FOUND"));
    }

    @Transactional
    public Notice createNotice(CreateNoticeRequest request) {
        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        return noticeRepository.save(notice);
    }

    @Transactional
    public Notice updateNotice(Long id, UpdateNoticeRequest request) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOTICE_NOT_FOUND"));

        notice.updateNotice(request.getTitle(), request.getContent());

        return notice;
    }

    @Transactional
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOTICE_NOT_FOUND"));

        notice.softDeleteData();
    }
}
