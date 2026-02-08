package com.zzx.server.common.security;

public record AuthUser(
        Long userId,
        String username,
        String role,
        Long shopId
) {
}