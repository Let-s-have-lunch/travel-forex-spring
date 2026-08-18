package com.lineacademy.travelforexspring.service;

import com.lineacademy.travelforexspring.domain.enums.UserRole;
import com.lineacademy.travelforexspring.domain.user.User;
import com.lineacademy.travelforexspring.dto.user.request.*;
import com.lineacademy.travelforexspring.repository.UserRepository;
import com.lineacademy.travelforexspring.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
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

        if (user.getDeletedAt() != null) {
            throw new RuntimeException("INVALID_CREDENTIAL");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("INVALID_CREDENTIAL");
        }

        return user;
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (user.getDeletedAt() != null) {
            throw new RuntimeException("USER_NOT_FOUND");
        }
        return user;
    }

    @Transactional
    public User updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (user.getDeletedAt() != null) {
            throw new RuntimeException("USER_NOT_FOUND");
        }

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

        if (user.getDeletedAt() != null) {
            throw new RuntimeException("USER_NOT_FOUND");
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void withdrawUser(Long userId, WithdrawUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        if (user.getDeletedAt() != null) throw new RuntimeException("USER_NOT_FOUND");

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("INVALID_CREDENTIAL");
        }

        user.softDeleteData();
    }
}
