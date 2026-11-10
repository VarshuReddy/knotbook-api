package com.knotbook.knotbook_api.dto.response;

import java.math.BigDecimal;

public record HookResponse(
        Long id,
        BigDecimal hookSizeMm
) {
}