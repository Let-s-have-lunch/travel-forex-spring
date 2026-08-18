package com.lineacademy.travelforexspring.domain.wallettransaction;

import com.lineacademy.travelforexspring.domain.common.BaseTimeEntity;
import com.lineacademy.travelforexspring.domain.enums.TransactionType;
import com.lineacademy.travelforexspring.domain.wallet.Wallet;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE wallet_transactions SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class WalletTransaction extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "applied_exchange_rate", precision = 10, scale = 4, nullable = false)
    private BigDecimal appliedExchangeRate;

    @Column(name = "converted_krw_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal convertedKrwAmount;

    @Column(name = "transaction_method", length = 100)
    private String transactionMethod;

    @Column(length = 255)
    private String memo;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Builder
    public WalletTransaction(
            Wallet wallet,
            TransactionType transactionType,
            BigDecimal amount,
            BigDecimal appliedExchangeRate,
            BigDecimal convertedKrwAmount,
            String transactionMethod,
            String memo,
            LocalDateTime transactionDate
    ) {
        this.wallet = wallet;
        this.transactionType = transactionType;
        this.amount = amount;
        this.appliedExchangeRate = appliedExchangeRate;
        this.convertedKrwAmount = convertedKrwAmount;
        this.transactionMethod = transactionMethod;
        this.memo = memo;
        this.transactionDate = transactionDate != null ? transactionDate : LocalDateTime.now();
    }
}