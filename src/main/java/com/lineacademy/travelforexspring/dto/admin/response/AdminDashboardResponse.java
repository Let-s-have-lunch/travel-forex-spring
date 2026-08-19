package com.lineacademy.travelforexspring.dto.admin.response;

import com.lineacademy.travelforexspring.domain.enums.UserRole;
import com.lineacademy.travelforexspring.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AdminDashboardResponse {

    private List<RecentUser> recentUsers;

    @Getter
    @Builder
    public static class RecentUser {
        private Long id;
        private String nickname;
        private String email;
        private UserRole role;
        private LocalDateTime createdAt;

        public static RecentUser from(User user) {
            return RecentUser.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .createdAt(user.getCreatedAt())
                    .build();
        }
    }
}
