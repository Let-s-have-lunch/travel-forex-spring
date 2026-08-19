package com.lineacademy.travelforexspring.dto.admin.inquiry.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InquiryAnswerRequest {
    @NotBlank(message = "답변은 필수 입력 사항입니다.")
    private String answer;
}
