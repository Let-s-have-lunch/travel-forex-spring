package com.lineacademy.travelforexspring.domain.user;

import com.lineacademy.travelforexspring.domain.common.BaseTimeEntity;
import com.lineacademy.travelforexspring.domain.enums.Gender;
import com.lineacademy.travelforexspring.domain.enums.UserRole;
import com.lineacademy.travelforexspring.domain.trip.Trip;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_nickname", columnList = "nickname"),
        @Index(name = "idx_user_phone", columnList = "phone_number")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE users SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 50, unique = true, nullable = false)
    private String nickname;

    @Column(name = "phone_number", length = 20, unique = true, nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate birthdate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    // TODO : Wallet과의 관계 매핑 필요

    @OneToMany(mappedBy = "user")
    private List<Trip> trips = new ArrayList<>();

    @Builder
    public User(String email, String password, String nickname, String phoneNumber, Gender gender, LocalDate birthdate, UserRole role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthdate = birthdate;
        this.role = role;
    }
}
