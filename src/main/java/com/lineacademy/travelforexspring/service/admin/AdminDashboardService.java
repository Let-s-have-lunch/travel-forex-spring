package com.lineacademy.travelforexspring.service.admin;

import com.lineacademy.travelforexspring.domain.user.User;
import com.lineacademy.travelforexspring.dto.admin.response.AdminDashboardResponse;
import com.lineacademy.travelforexspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboardSummary() {
        // Soft Delete(deletedAt IS NULL) 되지 않은 최신 가입자 5명 조회
        List<User> recentUsers = userRepository.findTop5ByDeletedAtIsNullOrderByCreatedAtDesc();

        List<AdminDashboardResponse.RecentUser> recentUserDtos = recentUsers.stream()
                .map(AdminDashboardResponse.RecentUser::from)
                .toList();

        return AdminDashboardResponse.builder()
                .recentUsers(recentUserDtos)
                .build();
    }
}