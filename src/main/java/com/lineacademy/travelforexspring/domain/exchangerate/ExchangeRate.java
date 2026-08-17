package com.lineacademy.travelforexspring.domain.exchangerate;

import com.lineacademy.travelforexspring.domain.common.BaseTimeEntity;
import com.lineacademy.travelforexspring.domain.enums.CurrencyCode;
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
@Table(name = "exchange_rates", indexes = {
        @Index(name = "idx_exchange_rate_currency_date", columnList = "currency, record_date")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 데이터가 실수로 지워졌을 때 복구할수 있도록 해주는 코드
// 이력 추적 및 증빙이 가능해져서 금융/결제 앱에서는 거의 필수적으로 쓰는 패턴임
@SQLDelete(sql = "UPDATE exchange_rates SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class ExchangeRate extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyCode currency;

    // 기준 환율 (원화 환산 기준가)
    @Column(name = "base_rate", precision = 10, scale = 4, nullable = false)
    private BigDecimal baseRate;

    @Column(name = "change_rate", precision = 5, scale = 4)
    private BigDecimal changeRate; // 전일 대비 등락률 (예: +0.42%)

    // 환율 기준 일시 (해당 시점 시세)
    @Column(name = "record_date", nullable = false)
    private LocalDateTime recordDate;

    @Builder
    public ExchangeRate(CurrencyCode currency, BigDecimal baseRate, BigDecimal changeRate, LocalDateTime recordDate) {
        this.currency = currency;
        this.baseRate = baseRate;
        this.changeRate = changeRate;
        this.recordDate = recordDate;
    }
}
