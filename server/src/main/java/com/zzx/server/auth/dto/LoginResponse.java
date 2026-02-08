package com.zzx.server.auth.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Long userId,
        String username,
        String role,
        Long shopId
) {
}