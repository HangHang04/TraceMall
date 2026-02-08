package com.zzx.server.common.audit.service;

import com.zzx.server.common.audit.mapper.AuditLogMapper;
import com.zzx.server.common.security.AuthUser;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogMapper auditLogMapper;

    public AuditLogService(AuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }

    public void log(AuthUser user,
                    String action,
                    String module,
                    String targetId,
                    String requestPath,
                    String requestMethod,
                    String requestBody,
                    String responseCode,
                    String ip) {
        try {
            auditLogMapper.insertAuditLog(
                    user == null ? null : user.userId(),
                    user == null ? "anonymous" : user.username(),
                    action,
                    module,
                    targetId,
                    requestPath,
                    requestMethod,
                    requestBody,
                    responseCode,
                    ip
            );
        } catch (Exception ignored) {
        }
    }
}