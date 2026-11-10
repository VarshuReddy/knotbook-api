package com.knotbook.knotbook_api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class HookRequest {

    @NotNull
    @DecimalMin("0.1")
    @Digits(integer = 3, fraction = 1)
    private BigDecimal hookSizeMm;
}
