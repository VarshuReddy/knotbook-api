package com.knotbook.knotbook_api.dto.response;

import java.time.LocalDateTime;

public record ProjectPhotoResponse(
        Long id,
        Long projectId,
        String imageUrl,
        String caption,
        boolean isCover,
        LocalDateTime takenAt,
        LocalDateTime createdAt
) {
}