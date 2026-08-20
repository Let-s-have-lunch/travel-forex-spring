package com.lineacademy.travelforexspring.domain.inquiry;

import com.lineacademy.travelforexspring.domain.common.BaseTimeEntity;
import com.lineacademy.travelforexspring.domain.enums.InquiryStatus;
import com.lineacademy.travelforexspring.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "inquiries", indexes = {
        @Index(name = "idx_inquiry_user_id", columnList = "user_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE inquiries SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Inquiry extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiryStatus status = InquiryStatus.PENDING;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Inquiry(
            User user,
            String title,
            String content
    ) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.status = InquiryStatus.PENDING;
    }

    public void addAnswer(String answer) {
        this.answer = answer;
        this.status = InquiryStatus.ANSWERED;
        this.answeredAt = LocalDateTime.now();
    }

    public void updateInquiry(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void deleteAnswer() {
        this.answer = null;
        this.answeredAt = null;
    }
}
