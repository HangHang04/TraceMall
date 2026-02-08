package com.zzx.server.common.audit.interceptor;

import com.zzx.server.common.audit.service.AuditLogService;
import com.zzx.server.common.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Component
public class AuditLogInterceptor implements HandlerInterceptor {

    private static final Set<String> SKIP_PATH_PREFIX = Set.of("/swagger-ui", "/api-docs", "/actuator");
    private final AuditLogService auditLogService;

    public AuditLogInterceptor(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        if (!path.startsWith("/api/") || "GET".equalsIgnoreCase(method)) {
            return;
        }
        if (SKIP_PATH_PREFIX.stream().anyMatch(path::startsWith)) {
            return;
        }
        String[] segments = path.split("/");
        String module = segments.length > 2 ? segments[2].toUpperCase() : "UNKNOWN";
        String action = method + "_" + module;
        String ip = request.getHeader("X-Forwarded-For") != null ? request.getHeader("X-Forwarded-For") : request.getRemoteAddr();
        auditLogService.log(
                SecurityUtils.currentUser(),
                action,
                module,
                null,
                path,
                method,
                null,
                String.valueOf(response.getStatus()),
                ip
        );
    }
}