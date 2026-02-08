package com.zzx.server.common.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTests {

    @Test
    void tokenShouldCarryClaims() {
        JwtTokenProvider provider = new JwtTokenProvider("TraceMallJwtSecretTraceMallJwtSecret2026", 120);
        AuthUser user = new AuthUser(1L, "consumer01", "CONSUMER", null);

        String token = provider.createToken(user);
        var claims = provider.parseClaims(token);

        Assertions.assertEquals("1", claims.getSubject());
        Assertions.assertEquals("consumer01", claims.get("username", String.class));
        Assertions.assertEquals("CONSUMER", claims.get("role", String.class));
    }
}