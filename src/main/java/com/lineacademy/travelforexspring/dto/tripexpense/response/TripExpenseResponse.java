package com.lineacademy.travelforexspring.dto.tripexpense.response;

import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import com.lineacademy.travelforexspring.domain.enums.ExpenseCategory;
import com.lineacademy.travelforexspring.domain.enums.PaymentMethod;
import com.lineacademy.travelforexspring.domain.tripexpense.TripExpense;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class TripExpenseResponse {
    private Long id;
    private CurrencyCode currency;
    private BigDecimal amount;
    private BigDecimal convertedKrwAmount;
    private ExpenseCategory category;
    private String merchant;
    private PaymentMethod paymentMethod;
    private boolean isWalletLinked;
    private Long walletId; // 👈 추가
    private String memo;
    private LocalDateTime expenseDate;

    public static TripExpenseResponse from(TripExpense expense) {
        Long linkedWalletId = null;
        if (expense.isWalletLinked() && expense.getWalletTransaction() != null) {
            linkedWalletId = expense.getWalletTransaction().getWallet().getId();
        }

        return TripExpenseResponse.builder()
                .id(expense.getId())
                .currency(expense.getCurrency())
                .amount(expense.getAmount())
                .convertedKrwAmount(expense.getConvertedKrwAmount())
                .category(expense.getCategory())
                .merchant(expense.getMerchant())
                .paymentMethod(expense.getPaymentMethod())
                .isWalletLinked(expense.isWalletLinked())
                .walletId(linkedWalletId) // 👈 매핑
                .memo(expense.getMemo())
                .expenseDate(expense.getExpenseDate())
                .build();
    }
}
