package com.lineacademy.travelforexspring.dto.notice.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateNoticeRequest {
    @NotBlank(message = "공지사항 제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "공지사항 내용을 입력해주세요.")
    private String content;
}
