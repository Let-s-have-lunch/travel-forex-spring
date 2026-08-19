package com.lineacademy.travelforexspring.dto.tripexpense.request;

import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import com.lineacademy.travelforexspring.domain.enums.ExpenseCategory;
import com.lineacademy.travelforexspring.domain.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UpdateTripExpenseRequest {
    @NotNull(message = "통화 코드를 선택해주세요.")
    private CurrencyCode currency;

    @NotNull(message = "지출 금액을 입력해주세요.")
    @DecimalMin(value = "0.01", message = "금액은 0보다 커야 합니다.")
    private BigDecimal amount;

    @NotNull(message = "원화 환산 금액을 입력해주세요.")
    private BigDecimal convertedKrwAmount;

    @NotNull(message = "지출 카테고리를 선택해주세요.")
    private ExpenseCategory category;

    private String merchant;

    @NotNull(message = "결제 수단을 선택해주세요.")
    private PaymentMethod paymentMethod;

    private String memo;

    @NotNull(message = "지출 일시를 입력해주세요.")
    private LocalDateTime expenseDate;
}
