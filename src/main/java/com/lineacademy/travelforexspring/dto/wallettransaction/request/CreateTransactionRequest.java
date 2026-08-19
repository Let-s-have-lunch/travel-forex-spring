package com.lineacademy.travelforexspring.dto.wallettransaction.request;

import com.lineacademy.travelforexspring.domain.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CreateTransactionRequest {

    @NotNull(message = "거래 타입을 선택해주세요. (DEPOSIT / WITHDRAWAL)")
    private TransactionType transactionType;

    @NotNull(message = "거래 금액을 입력해주세요.")
    @DecimalMin(value = "0.01", message = "거래 금액은 0보다 커야 합니다.")
    private BigDecimal amount;

    @NotNull(message = "적용 환율을 입력해주세요.")
    private BigDecimal appliedExchangeRate;

    @NotNull(message = "원화 환산 금액을 입력해주세요.")
    private BigDecimal convertedKrwAmount;

    private String transactionMethod;

    private String memo;

    @NotNull(message = "거래 일시를 입력해주세요.")
    private LocalDateTime transactionDate;
}