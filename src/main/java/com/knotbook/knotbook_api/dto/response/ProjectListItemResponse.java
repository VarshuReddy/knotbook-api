package com.knotbook.knotbook_api.dto.response;

import com.knotbook.knotbook_api.enity.ProjectStatus;

import java.time.LocalDate;

public record ProjectListItemResponse(
        Long id,
        String title,
        String category,
        ProjectStatus status,
        String coverImageUrl,
        LocalDate startDate,
        Long totalTimeMinutes
) {
}
