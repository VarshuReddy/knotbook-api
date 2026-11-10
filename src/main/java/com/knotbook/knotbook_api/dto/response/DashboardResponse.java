package com.knotbook.knotbook_api.dto.response;

import java.util.List;

public record DashboardResponse(
        long totalProjects,
        long inProgressProjects,
        long completedProjects,
        Long totalTimeMinutes,
        List<ProjectListItemResponse> recentProjects
) {}
