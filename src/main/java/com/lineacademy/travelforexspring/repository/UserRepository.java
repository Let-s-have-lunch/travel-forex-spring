package com.lineacademy.travelforexspring.repository;

import com.lineacademy.travelforexspring.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByNicknameAndIdNot(String nickname, Long id);
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

    Optional<User> findByEmail(String email);

    List<User> findTop5ByOrderByCreatedAtDesc();

    Page<User> findAllByOrderByIdDesc(Pageable pageable);
}
