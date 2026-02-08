package com.zzx.server.trace.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyTraceRequest(
        @NotBlank(message = "traceId is required") String traceId,
        @NotBlank(message = "signature is required") String signature,
        String geo,
        String deviceFingerprint,
        String ip
) {
}