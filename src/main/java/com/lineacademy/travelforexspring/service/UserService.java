package com.lineacademy.travelforexspring.service;

import com.lineacademy.travelforexspring.domain.enums.UserRole;
import com.lineacademy.travelforexspring.domain.user.User;
import com.lineacademy.travelforexspring.dto.admin.user.request.AdminUserUpdateRequest;
import com.lineacademy.travelforexspring.dto.user.request.*;
import com.lineacademy.travelforexspring.repository.UserRepository;
import com.lineacademy.travelforexspring.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("ALREADY_EXISTS_EMAIL");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("ALREADY_EXISTS_NICKNAME");
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("ALREADY_EXISTS_PHONE");
        }

        LocalDate parsedBirthdate = null;
        if (request.getBirthdate() != null && !request.getBirthdate().isBlank()) {
            parsedBirthdate = LocalDate.parse(request.getBirthdate(), DateTimeFormatter.ISO_DATE);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender())
                .birthdate(parsedBirthdate)
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        return user;
    }

    @Transactional(readOnly = true)
    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("INVALID_CREDENTIAL"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("INVALID_CREDENTIAL");
        }

        return user;
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
    }

    @Transactional
    public User updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (!user.getNickname().equals(request.getNickname()) &&
                userRepository.existsByNickname(request.getNickname())) {
            throw new RuntimeException("ALREADY_EXISTS_NICKNAME");
        }

        if (!user.getPhoneNumber().equals(request.getPhoneNumber()) &&
                userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("ALREADY_EXISTS_PHONE");
        }

        LocalDate parsedBirthdate = null;
        if (request.getBirthdate() != null && !request.getBirthdate().isBlank()) {
            parsedBirthdate = LocalDate.parse(request.getBirthdate(), DateTimeFormatter.ISO_DATE);
        }

        user.updateProfile(
                request.getNickname(),
                request.getPhoneNumber(),
                request.getGender(),
                parsedBirthdate
        );

        return user;
    }

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (!passwordEncoder.matches(request.getSpringPassword(), user.getPassword())) {
            throw new RuntimeException("INVALID_PASSWORD");
        }

        user.updatePassword(passwordEncoder.encode(request.getPassword()));
    }

    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        user.updatePassword(passwordEncoder.encode(newPassword));
    }
    // ⭐ 유저 본인 탈퇴
    @Transactional
    public void withdrawUser(Long userId, WithdrawUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("INVALID_CREDENTIAL");
        }

        user.softDeleteData();
    }


    @Transactional(readOnly = true)
    public Page<User> getUserList(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return userRepository.findAllByOrderByIdDesc(pageable);
    }

    @Transactional
    public User adminUpdateUser(Long targetUserId, AdminUserUpdateRequest request) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        // 1. 닉네임 변경 시 중복 검사
        if (request.getNickname() != null && !request.getNickname().equals(targetUser.getNickname())) {
            if (userRepository.existsByNicknameAndIdNot(request.getNickname(), targetUserId)) {
                throw new RuntimeException("ALREADY_EXISTS_NICKNAME");
            }
            targetUser.updateNickname(request.getNickname());
        }

        // 2. 이메일 변경 시 중복 검사
        if (request.getEmail() != null && !request.getEmail().equals(targetUser.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new RuntimeException("ALREADY_EXISTS_EMAIL");
            }
            targetUser.updateEmail(request.getEmail());
        }

        // 3. 전화번호 변경 시 중복 검사
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(targetUser.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumberAndIdNot(request.getPhoneNumber(), targetUserId)) {
                throw new RuntimeException("ALREADY_EXISTS_PHONE");
            }
            targetUser.updatePhoneNumber(request.getPhoneNumber());
        }

        // 4. 비밀번호 변경 시 암호화
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            targetUser.updatePassword(passwordEncoder.encode(request.getPassword()));
        }

        // 5. 성별 변경
        if (request.getGender() != null) {
            targetUser.updateGender(request.getGender());
        }

        // 6. 생년월일 변경
        if (request.getBirthdate() != null) {
            targetUser.updateBirthdate(request.getBirthdate());
        }

        // 7. 권한(Role) 변경
        if (request.getRole() != null) {
            targetUser.updateRole(request.getRole());
        }

        return targetUser;
    }

    // ⭐ 관리자의 유저 삭제
    @Transactional
    public User adminDeleteUser(Long targetUserId) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        targetUser.softDeleteData();

        return targetUser;
    }
}
