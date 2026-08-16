package com.lineacademy.travelforexspring.dto.trip.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class UpdateTripRequest {
    @NotBlank(message = "여행 제목을 입력해주세요.")
    private String title;

    @NotNull(message = "여행 시작일을 입력해주세요.")
    private LocalDate startDate;

    @NotNull(message = "여행 종료일을 입력해주세요.")
    private LocalDate endDate;

    @NotNull(message = "예산을 입력해주세요.")
    @DecimalMin(value = "0.0", message = "예산은 0 이상이어야 합니다.")
    private BigDecimal budgetKrw;
}
