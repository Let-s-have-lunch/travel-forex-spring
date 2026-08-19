package com.lineacademy.travelforexspring.dto.wallettransaction.response;

import com.lineacademy.travelforexspring.domain.enums.TransactionType;
import com.lineacademy.travelforexspring.domain.wallettransaction.WalletTransaction;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class TransactionResponse {
    private Long id;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal appliedExchangeRate;
    private BigDecimal convertedKrwAmount;
    private String transactionMethod;
    private String memo;
    private LocalDateTime transactionDate;

    public static TransactionResponse from(WalletTransaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .appliedExchangeRate(transaction.getAppliedExchangeRate())
                .convertedKrwAmount(transaction.getConvertedKrwAmount())
                .transactionMethod(transaction.getTransactionMethod())
                .memo(transaction.getMemo())
                .transactionDate(transaction.getTransactionDate())
                .build();
    }
}