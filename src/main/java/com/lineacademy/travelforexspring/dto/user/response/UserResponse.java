package com.lineacademy.travelforexspring.dto.user.response;

import com.lineacademy.travelforexspring.domain.enums.Gender;
import com.lineacademy.travelforexspring.domain.enums.UserRole;
import com.lineacademy.travelforexspring.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;
    private String phoneNumber;
    private Gender gender;
    private LocalDate birthdate;
    private UserRole role;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .birthdate(user.getBirthdate())
                .role(user.getRole())
                .build();
    }
}
