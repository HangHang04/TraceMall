package com.zzx.server.trace.dto;

import java.time.LocalDateTime;

public record TracePayload(
        String traceId,
        String batchNo,
        String signature,
        LocalDateTime issuedAt
) {
}