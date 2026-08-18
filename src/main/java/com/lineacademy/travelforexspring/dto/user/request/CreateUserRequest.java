package com.lineacademy.travelforexspring.dto.user.request;

import com.lineacademy.travelforexspring.domain.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateUserRequest {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 6, message = "비밀번호는 6자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 10, message = "닉네임은 2~10자 사이여야 합니다.")
    private String nickname;

    @NotBlank(message = "전화번호를 입력해주세요.")
    private String phoneNumber;

    @NotNull(message = "성별을 선택해주세요.")
    private Gender gender;

    @NotNull(message = "생년월일을 입력해주세요.")
    private String birthdate;
}
