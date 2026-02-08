package com.zzx.server.commerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BatchEventCreateRequest(
        @NotNull(message = "batchId is required") Long batchId,
        @NotBlank(message = "eventType is required") String eventType,
        @NotNull(message = "eventTime is required") LocalDateTime eventTime,
        @NotBlank(message = "location is required") String location,
        String payloadJson
) {
}