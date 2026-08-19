package com.lineacademy.travelforexspring.domain.wallet;

import com.lineacademy.travelforexspring.domain.common.BaseTimeEntity;
import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
import com.lineacademy.travelforexspring.domain.user.User;
import com.lineacademy.travelforexspring.domain.wallettransaction.WalletTransaction;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wallets", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_currency", columnNames = {"user_id", "currency"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE wallets SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Wallet extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. User 관계 설정 (N:1 단방향/양방향)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CurrencyCode currency;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    // 2. WalletTransaction 관계 설정 (1:N 양방향)
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    private List<WalletTransaction> transactions = new ArrayList<>();

    @Builder
    public Wallet(
            User user,
            CurrencyCode currency,
            BigDecimal balance
    ) {
        this.user = user;
        this.currency = currency;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
    }

    // ==========================================
    // 비즈니스 로직 메서드 (홈 화면 입출금 연동 시 사용)
    // ==========================================

    // 입금: 잔액 증가
    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("입금 금액은 0보다 커야 합니다.");
        }
        this.balance = this.balance.add(amount);
    }

    // 출금/지출: 잔액 차감
    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("출금 금액은 0보다 커야 합니다.");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalStateException("지갑 잔액이 부족합니다.");
        }
        this.balance = this.balance.subtract(amount);
    }
    public void addBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
    public void subtractBalance(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new RuntimeException("INSUFFICIENT_BALANCE");
        }
        this.balance = this.balance.subtract(amount);
    }
}