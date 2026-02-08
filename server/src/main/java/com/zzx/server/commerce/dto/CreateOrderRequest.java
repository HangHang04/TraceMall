package com.zzx.server.commerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        @NotEmpty(message = "items is required") List<@Valid OrderItem> items,
        String shippingAddress,
        String remark
) {
    public record OrderItem(
            @NotNull(message = "batchId is required") Long batchId,
            @NotNull(message = "quantity is required") @DecimalMin(value = "0.001", message = "quantity must > 0") BigDecimal quantity
    ) {
    }
}