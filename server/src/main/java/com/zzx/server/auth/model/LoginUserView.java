package com.zzx.server.auth.model;

public record LoginUserView(
        Long userId,
        String username,
        String password,
        String nickname,
        String roleCode,
        Long shopId
) {
}