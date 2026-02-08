package com.zzx.server.trace.dto;

import java.util.Map;

public record VerifyResult(
        String status,
        String reason,
        String riskLevel,
        Map<String, Object> batchSummary,
        boolean anchorMatched
) {
}