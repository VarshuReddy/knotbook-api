package com.knotbook.knotbook_api.dto.response;

import com.knotbook.knotbook_api.enity.ProjectStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ProjectDetailResponse(
        Long id,
        String title,
        String description,
        String category,
        ProjectStatus status,
        LocalDate startDate,
        LocalDate endDate,
        Long totalTimeMinutes,
        List<YarnResponse> yarns,
        List<HookResponse> hooks,
        List<ProjectPhotoResponse> photos,
        ProjectNoteResponse notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}