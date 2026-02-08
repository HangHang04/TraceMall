package com.zzx.server.common.api;

public enum ErrorCode {
    SUCCESS("0"),
    UNAUTHORIZED("A001"),
    FORBIDDEN("A002"),
    VALIDATION_ERROR("A003"),
    BUSINESS_ERROR("B001"),
    TRACE_VERIFY_FAILED("T001"),
    SYSTEM_ERROR("S001");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
