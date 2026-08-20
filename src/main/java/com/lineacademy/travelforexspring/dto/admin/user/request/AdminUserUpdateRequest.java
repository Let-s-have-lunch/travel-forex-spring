package com.lineacademy.travelforexspring.dto.admin.user.request;

import com.lineacademy.travelforexspring.domain.enums.Gender;
import com.lineacademy.travelforexspring.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AdminUserUpdateRequest {

    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하이어야 합니다.")
    private String nickname;

    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String password;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    private String phoneNumber;

    private Gender gender;

    private LocalDate birthdate;

    private UserRole role;
}
