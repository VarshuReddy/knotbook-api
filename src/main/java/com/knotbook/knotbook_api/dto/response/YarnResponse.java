package com.knotbook.knotbook_api.dto.response;

import java.math.BigDecimal;

public record YarnResponse(
        Long id,
        String color,
        String weight,
        String type
) {
}