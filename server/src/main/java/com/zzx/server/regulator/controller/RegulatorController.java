package com.zzx.server.regulator.controller;

import com.zzx.server.common.api.ApiResponse;
import com.zzx.server.common.api.PageResult;
import com.zzx.server.regulator.service.RegulatorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/regulator")
@PreAuthorize("hasRole('REGULATOR')")
public class RegulatorController {

    private final RegulatorService regulatorService;

    public RegulatorController(RegulatorService regulatorService) {
        this.regulatorService = regulatorService;
    }

    @GetMapping("/alerts")
    public ApiResponse<PageResult<Map<String, Object>>> alerts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.success(regulatorService.listRiskAlerts(page, size));
    }

    @GetMapping("/audits")
    public ApiResponse<PageResult<Map<String, Object>>> audits(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.success(regulatorService.listAuditLogs(page, size));
    }
}