package com.lineacademy.travelforexspring.domain.trip;

import com.lineacademy.travelforexspring.domain.common.BaseTimeEntity;
import com.lineacademy.travelforexspring.domain.tripexpense.TripExpense;
import com.lineacademy.travelforexspring.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trips")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trip extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // 외화를 실시간 환율로 원화로 환산할 때 소수점 이하 금액이 발생할 수 있다.
    // 소수점 버림/반올림 처리를 유연하게 두기 위해 소수점을 열어놓았다.
    // 원화에 소수점이 어색하다면, 화면에 표시할 때는 소수점을 버리고 정수로 포맷팅 하면 된다.
    @Column(name = "budget_krw", precision = 15, scale = 2, nullable = false)
    private BigDecimal budgetKrw = BigDecimal.ZERO;

    @OneToMany(mappedBy = "trip")
    private List<TripExpense> expenses = new ArrayList<>();

    @Builder
    public Trip(User user, String title, LocalDate startDate, LocalDate endDate, BigDecimal budgetKrw) {
        this.user = user;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        // 빌더패턴으로 객체를 생성할 때, 실수로 budgetKrw 값을 넘기지 않으면 파라미터로 null이 들어온다.
        // budget_krw는 nullable=false이자 default: 0인데,
        // JPA 엔티티 필드에 null이 들어가면 NotNull 제약조건 에러가 발생한다.
        // null을 대입해 버리면 초기화된 기본값이 지워지고 null로 덮어씌어져서
        // 이를 막고 값이 안들어오면 무조건 0으로 채운다는 방엉코드를 작성한다.
        this.budgetKrw = budgetKrw != null ? budgetKrw : BigDecimal.ZERO;
    }
}
