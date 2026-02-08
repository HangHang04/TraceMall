package com.zzx.server.commerce.controller;

import com.zzx.server.commerce.dto.CreateOrderRequest;
import com.zzx.server.commerce.dto.PayOrderRequest;
import com.zzx.server.commerce.service.OrderService;
import com.zzx.server.common.api.ApiResponse;
import com.zzx.server.common.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@PreAuthorize("hasRole('CONSUMER')")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        return ApiResponse.success(orderService.placeOrder(request, SecurityUtils.currentUser()));
    }

    @PostMapping("/{orderNo}/pay")
    public ApiResponse<Map<String, Object>> payOrder(@PathVariable String orderNo,
                                                     @RequestBody(required = false) PayOrderRequest request) {
        PayOrderRequest payOrderRequest = request == null ? new PayOrderRequest(null) : request;
        return ApiResponse.success(orderService.payOrder(orderNo, payOrderRequest, SecurityUtils.currentUser()));
    }

    @GetMapping("/mine")
    public ApiResponse<List<Map<String, Object>>> myOrders() {
        return ApiResponse.success(orderService.listMyOrders(SecurityUtils.currentUser()));
    }
}