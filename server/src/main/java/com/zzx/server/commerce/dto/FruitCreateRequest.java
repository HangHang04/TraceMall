package com.zzx.server.commerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record FruitCreateRequest(
        @NotBlank(message = "fruitName is required") String fruitName,
        @NotBlank(message = "category is required") String category,
        String origin,
        String unit,
        @NotNull(message = "unitPrice is required") @DecimalMin(value = "0.01", message = "unitPrice must > 0") BigDecimal unitPrice,
        String description
) {
}