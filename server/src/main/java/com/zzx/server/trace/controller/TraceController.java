package com.zzx.server.trace.controller;

import com.zzx.server.common.api.ApiResponse;
import com.zzx.server.trace.dto.VerifyResult;
import com.zzx.server.trace.dto.VerifyTraceRequest;
import com.zzx.server.trace.service.TraceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/trace")
public class TraceController {

    private final TraceService traceService;

    public TraceController(TraceService traceService) {
        this.traceService = traceService;
    }

    @GetMapping("/{traceId}")
    public ApiResponse<Map<String, Object>> traceDetail(@PathVariable String traceId) {
        return ApiResponse.success(traceService.traceDetail(traceId));
    }

    @PostMapping("/verify")
    public ApiResponse<VerifyResult> verify(@RequestBody @Valid VerifyTraceRequest request) {
        return ApiResponse.success(traceService.verify(request));
    }
}