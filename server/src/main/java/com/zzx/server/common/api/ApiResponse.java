package com.zzx.server.common.api;

import java.time.Instant;

public record ApiResponse<T>(
        String code,
        String message,
        T data,
        String traceId,
        Instant timestamp
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ErrorCode.SUCCESS.code(), "OK", data, null, Instant.now());
    }

    public static <T> ApiResponse<T> success(T data, String traceId) {
        return new ApiResponse<>(ErrorCode.SUCCESS.code(), "OK", data, traceId, Instant.now());
    }

    public static ApiResponse<Void> fail(ErrorCode errorCode, String message, String traceId) {
        return new ApiResponse<>(errorCode.code(), message, null, traceId, Instant.now());
    }
}
