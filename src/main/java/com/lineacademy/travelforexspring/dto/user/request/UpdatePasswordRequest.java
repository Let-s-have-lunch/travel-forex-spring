package com.lineacademy.travelforexspring.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequest {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String prevPassword;

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    @Size(min = 6, message = "비밀번호 확인은 최소 6자 이상이어야 합니다.")
    private String confirmPassword;
}