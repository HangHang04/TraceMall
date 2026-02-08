package com.zzx.server.commerce.controller;

import com.zzx.server.commerce.service.MallService;
import com.zzx.server.common.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fruits")
public class MallController {

    private final MallService mallService;

    public MallController(MallService mallService) {
        this.mallService = mallService;
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> listFruits() {
        return ApiResponse.success(mallService.listFruits());
    }

    @GetMapping("/{fruitId}")
    public ApiResponse<Map<String, Object>> getFruit(@PathVariable Long fruitId) {
        return ApiResponse.success(mallService.getFruitDetail(fruitId));
    }
}