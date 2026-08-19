package com.lineacademy.travelforexspring.repository;

import com.lineacademy.travelforexspring.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // ==========================================
    // 기존 트래블 프로젝트 전용 메소드 (유지)
    // ==========================================
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);


    // ==========================================
    // 어드민 기능 전용 추가 메소드
    // ==========================================

    // 이메일로 삭제되지 않은 유저 조회
    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    // 유저 수정 시 본인 제외 중복 체크 (Soft Delete 고려)
    boolean existsByNicknameAndIdNotAndDeletedAtIsNull(String nickname, Long id);

    boolean existsByEmailAndIdNotAndDeletedAtIsNull(String email, Long id);

    boolean existsByPhoneNumberAndIdNotAndDeletedAtIsNull(String phoneNumber, Long id);

    // 어드민 대시보드용: 삭제되지 않은 최근 가입 유저 5명
    List<User> findTop5ByDeletedAtIsNullOrderByCreatedAtDesc();

    // 어드민 유저 관리용: 삭제되지 않은 회원 목록 페이징
    Page<User> findAllByDeletedAtIsNullOrderByIdDesc(Pageable pageable);
}
