package com.zzx.server.regulator.service;

import com.zzx.server.common.api.PageResult;
import com.zzx.server.common.audit.mapper.AuditLogMapper;
import com.zzx.server.regulator.mapper.RegulatorMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RegulatorService {

    private final RegulatorMapper regulatorMapper;
    private final AuditLogMapper auditLogMapper;

    public RegulatorService(RegulatorMapper regulatorMapper, AuditLogMapper auditLogMapper) {
        this.regulatorMapper = regulatorMapper;
        this.auditLogMapper = auditLogMapper;
    }

    public PageResult<Map<String, Object>> listRiskAlerts(int page, int size) {
        int offset = Math.max(page - 1, 0) * size;
        return new PageResult<>(regulatorMapper.listRiskAlerts(offset, size), regulatorMapper.countRiskAlerts(), page, size);
    }

    public PageResult<Map<String, Object>> listAuditLogs(int page, int size) {
        int offset = Math.max(page - 1, 0) * size;
        return new PageResult<>(auditLogMapper.listAuditLogs(offset, size), auditLogMapper.countAuditLogs(), page, size);
    }
}