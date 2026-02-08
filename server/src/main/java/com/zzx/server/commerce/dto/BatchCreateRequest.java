package com.zzx.server.commerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BatchCreateRequest(
        @NotNull(message = "fruitId is required") Long fruitId,
        @NotBlank(message = "batchNo is required") String batchNo,
        String traceId,
        @NotNull(message = "harvestDate is required") LocalDate harvestDate,
        LocalDate expireDate,
        @NotNull(message = "quantity is required") @DecimalMin(value = "0.001", message = "quantity must > 0") BigDecimal quantity
) {
}