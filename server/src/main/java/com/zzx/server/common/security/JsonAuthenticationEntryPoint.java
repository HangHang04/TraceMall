package com.zzx.server.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzx.server.common.api.ApiResponse;
import com.zzx.server.common.api.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JsonAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String traceId = request.getAttribute("X-Trace-Id") != null
                ? String.valueOf(request.getAttribute("X-Trace-Id"))
                : request.getHeader("X-Trace-Id");
        response.getWriter().write(objectMapper.writeValueAsString(
                ApiResponse.fail(ErrorCode.UNAUTHORIZED, "Unauthorized", traceId)
        ));
    }
}
