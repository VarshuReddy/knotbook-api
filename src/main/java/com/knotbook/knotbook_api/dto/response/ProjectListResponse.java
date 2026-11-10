package com.knotbook.knotbook_api.dto.response;


import java.util.List;

public record ProjectListResponse(
        List<ProjectListItemResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
