package com.lineacademy.travelforexspring.domain.tripexpense;

import com.lineacademy.travelforexspring.domain.common.BaseTimeEntity;
import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import com.lineacademy.travelforexspring.domain.enums.ExpenseCategory;
import com.lineacademy.travelforexspring.domain.enums.PaymentMethod;
import com.lineacademy.travelforexspring.domain.trip.Trip;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip_expenses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripExpense extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyCode currency;

    // 지출 금액 (현지 통화)
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "converted_krw_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal convertedKrwAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseCategory category;

    // 가맹점(사용처) : 현지식당, 스타벅스, 지하철 등..
    @Column(length = 255)
    private String merchant;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "is_wallet_linked", nullable = false)
    private boolean isWalletLinked = false;

    @Column(length = 255)
    private String memo;

    @Column(name = "expense_date", nullable = false)
    private LocalDateTime expenseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    // TODO : WalletTransaction과 관계 설정

    @Builder
    public TripExpense(
            Trip trip,
            CurrencyCode currency,
            BigDecimal amount,
            BigDecimal convertedKrwAmount,
            ExpenseCategory category,
            String merchant,
            PaymentMethod paymentMethod,
            boolean isWalletLinked,
//            WalletTransaction walletTransaction,
            String memo,
            LocalDateTime expenseDate
    ) {
        this.trip = trip;
        this.currency = currency;
        this.amount = amount;
        this.convertedKrwAmount = convertedKrwAmount;
        this.category = category;
        this.merchant = merchant;
        this.paymentMethod = paymentMethod;
        this.isWalletLinked = isWalletLinked;
//        this.walletTransaction = walletTransaction;
        this.memo = memo;
        this.expenseDate = expenseDate;
    }


    public void updateExpense(
            CurrencyCode currency, BigDecimal amount, BigDecimal convertedKrwAmount,
            ExpenseCategory category, String merchant, PaymentMethod paymentMethod,
            String memo, LocalDateTime expenseDate
    ) {
        this.currency = currency;
        this.amount = amount;
        this.convertedKrwAmount = convertedKrwAmount;
        this.category = category;
        this.merchant = merchant;
        this.paymentMethod = paymentMethod;
        this.memo = memo;
        this.expenseDate = expenseDate;
    }
}
