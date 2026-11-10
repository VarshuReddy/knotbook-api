package com.knotbook.knotbook_api.service;

import com.knotbook.knotbook_api.dto.response.DashboardResponse;
import com.knotbook.knotbook_api.dto.response.ProjectListItemResponse;
import com.knotbook.knotbook_api.enity.ProjectStatus;
import com.knotbook.knotbook_api.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProjectRepository projectRepository;
    private final ProjectService projectService;

    public DashboardResponse getDashboard(Long userId) {

        long total = projectRepository.countByUserIdAndIsDeletedFalse(userId);

        long inProgress = projectRepository
                .countByUserIdAndStatusAndIsDeletedFalse(
                        userId, ProjectStatus.IN_PROGRESS
                );

        long completed = projectRepository
                .countByUserIdAndStatusAndIsDeletedFalse(
                        userId, ProjectStatus.FINISHED
                );

        Long totalTime = projectRepository.getTotalTimeByUserId(userId);

        List<ProjectListItemResponse> recent =
                projectService.getRecentProjects(userId, 5);

        return new DashboardResponse(
                total,
                inProgress,
                completed,
                totalTime,
                recent
        );
    }
}