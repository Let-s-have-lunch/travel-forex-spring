package com.lineacademy.travelforexspring.domain.trip;

import com.lineacademy.travelforexspring.domain.common.BaseTimeEntity;
import com.lineacademy.travelforexspring.domain.tripexpense.TripExpense;
import com.lineacademy.travelforexspring.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trips")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE trips SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Trip extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "budget_krw", precision = 15, scale = 2, nullable = false)
    private BigDecimal budgetKrw = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "trip")
    private List<TripExpense> expenses = new ArrayList<>();

    @Builder
    public Trip(User user, String title, LocalDate startDate, LocalDate endDate, BigDecimal budgetKrw) {
        this.user = user;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budgetKrw = budgetKrw != null ? budgetKrw : BigDecimal.ZERO;
    }

    public void updateTrip(String title, LocalDate startDate, LocalDate endDate, BigDecimal budgetKrw) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budgetKrw = budgetKrw != null ? budgetKrw : BigDecimal.ZERO;
    }
}
