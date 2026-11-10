package com.knotbook.knotbook_api.dto.response;


import java.time.LocalDateTime;

public record ProjectNoteResponse(
        Long id,
        Long projectId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}