package com.zzx.server.commerce.controller;

import com.zzx.server.commerce.dto.BatchCreateRequest;
import com.zzx.server.commerce.dto.BatchEventCreateRequest;
import com.zzx.server.commerce.dto.FruitCreateRequest;
import com.zzx.server.commerce.service.MerchantService;
import com.zzx.server.common.api.ApiResponse;
import com.zzx.server.common.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant")
@PreAuthorize("hasRole('MERCHANT')")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @GetMapping("/fruits")
    public ApiResponse<List<Map<String, Object>>> listMerchantFruits() {
        return ApiResponse.success(merchantService.listMerchantFruits(SecurityUtils.currentUser()));
    }

    @PostMapping("/fruits")
    public ApiResponse<Void> createFruit(@RequestBody @Valid FruitCreateRequest request) {
        merchantService.createFruit(request, SecurityUtils.currentUser());
        return ApiResponse.success(null);
    }

    @PostMapping("/batches")
    public ApiResponse<Map<String, Object>> createBatch(@RequestBody @Valid BatchCreateRequest request) {
        return ApiResponse.success(merchantService.createBatch(request, SecurityUtils.currentUser()));
    }

    @PostMapping("/batch-events")
    public ApiResponse<Map<String, Object>> createBatchEvent(@RequestBody @Valid BatchEventCreateRequest request) {
        return ApiResponse.success(merchantService.createBatchEvent(request, SecurityUtils.currentUser()));
    }
}