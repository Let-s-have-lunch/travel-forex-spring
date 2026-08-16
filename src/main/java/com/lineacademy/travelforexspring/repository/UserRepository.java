package com.lineacademy.travelforexspring.repository;

// 유저 담당 팀원분께
// 관계 맺는것때문에 오류가 나서 임시로 생성한 파일입니다.
// 유저 작업 시작하면 내용 삭제하고 다시 작성해주시면 될것 같아요. !

import com.lineacademy.travelforexspring.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);
}
