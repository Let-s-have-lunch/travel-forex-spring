package com.lineacademy.travelforexspring.repository;

import com.lineacademy.travelforexspring.domain.inquiry.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    // 단건 조회 (User 정보 Fetch Join & Soft Delete 된 건 제외)
    @Query("SELECT i FROM Inquiry i JOIN FETCH i.user WHERE i.id = :id AND i.deletedAt IS NULL")
    Optional<Inquiry> findByIdWithUser(@Param("id") Long id);

    // 목록 조회 (N+1 방지 Fetch Join 및 countQuery 분리, Soft Delete 제외)
    @Query(value = "SELECT i FROM Inquiry i JOIN FETCH i.user WHERE i.deletedAt IS NULL ORDER BY i.id DESC",
            countQuery = "SELECT count(i) FROM Inquiry i WHERE i.deletedAt IS NULL")
    Page<Inquiry> findAllWithUserByOrderByIdDesc(Pageable pageable);
}
