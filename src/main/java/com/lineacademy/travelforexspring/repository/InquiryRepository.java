package com.lineacademy.travelforexspring.repository;

import com.lineacademy.travelforexspring.domain.inquiry.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    Page<Inquiry> findAllByUserIdOrderByIdDesc(Long userId, Pageable pageable);

    Optional<Inquiry> findByIdAndUserId(Long id, Long userId);

    Page<Inquiry> findAllByOrderByIdDesc(Pageable pageable);
}
