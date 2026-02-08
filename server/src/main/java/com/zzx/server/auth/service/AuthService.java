package com.zzx.server.auth.service;

import com.zzx.server.auth.dto.LoginRequest;
import com.zzx.server.auth.dto.LoginResponse;
import com.zzx.server.auth.mapper.AuthMapper;
import com.zzx.server.auth.model.LoginUserView;
import com.zzx.server.common.api.ErrorCode;
import com.zzx.server.common.exception.BizException;
import com.zzx.server.common.security.AuthUser;
import com.zzx.server.common.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(AuthMapper authMapper, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.authMapper = authMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse login(LoginRequest request) {
        LoginUserView user = authMapper.findByUsername(request.username());
        if (user == null || !passwordEncoder.matches(request.password(), user.password())) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "Invalid username or password");
        }

        AuthUser authUser = new AuthUser(user.userId(), user.username(), user.roleCode(), user.shopId());
        String token = jwtTokenProvider.createToken(authUser);
        return new LoginResponse(token, "Bearer", authUser.userId(), authUser.username(), authUser.role(), authUser.shopId());
    }
}