package com.zzx.server.commerce.service;

import com.zzx.server.commerce.dto.CreateOrderRequest;
import com.zzx.server.commerce.dto.PayOrderRequest;
import com.zzx.server.commerce.mapper.CommerceMapper;
import com.zzx.server.commerce.model.OrderStatus;
import com.zzx.server.common.api.ErrorCode;
import com.zzx.server.common.exception.BizException;
import com.zzx.server.common.security.AuthUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderService {

    private final CommerceMapper commerceMapper;

    public OrderService(CommerceMapper commerceMapper) {
        this.commerceMapper = commerceMapper;
    }

    @Transactional
    public Map<String, Object> placeOrder(CreateOrderRequest request, AuthUser user) {
        if (user == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "Unauthorized");
        }

        String orderNo = generateOrderNo();
        BigDecimal total = BigDecimal.ZERO;

        commerceMapper.insertOrder(
                user.userId(),
                orderNo,
                OrderStatus.PENDING_PAYMENT.name(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                request.shippingAddress(),
                request.remark()
        );

        Long orderId = commerceMapper.findOrderIdByOrderNo(orderNo);
        if (orderId == null) {
            throw new BizException(ErrorCode.SYSTEM_ERROR, "order creation failed");
        }

        for (CreateOrderRequest.OrderItem item : request.items()) {
            Map<String, Object> snapshot = commerceMapper.findBatchOrderSnapshot(item.batchId());
            if (snapshot == null) {
                throw new BizException(ErrorCode.BUSINESS_ERROR, "batch not found: " + item.batchId());
            }
            BigDecimal stock = toDecimal(snapshot.get("remainingQuantity"));
            if (stock.compareTo(item.quantity()) < 0) {
                throw new BizException(ErrorCode.BUSINESS_ERROR, "insufficient stock for batch: " + item.batchId());
            }

            BigDecimal unitPrice = toDecimal(snapshot.get("unitPrice"));
            BigDecimal itemTotal = unitPrice.multiply(item.quantity()).setScale(2, RoundingMode.HALF_UP);
            total = total.add(itemTotal);

            int stockRows = commerceMapper.decreaseInventory(item.batchId(), item.quantity());
            int batchRows = commerceMapper.decreaseBatchRemaining(item.batchId(), item.quantity());
            if (stockRows != 1 || batchRows != 1) {
                throw new BizException(ErrorCode.BUSINESS_ERROR, "stock deduction failed for batch: " + item.batchId());
            }

            Long fruitId = ((Number) snapshot.get("fruitId")).longValue();
            commerceMapper.insertOrderItem(orderId, fruitId, item.batchId(), item.quantity(), unitPrice, itemTotal);
        }

        BigDecimal payable = total.setScale(2, RoundingMode.HALF_UP);
        commerceMapper.updateOrderAmount(orderNo, payable, payable);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderNo", orderNo);
        result.put("status", OrderStatus.PENDING_PAYMENT.name());
        result.put("payableAmount", payable);
        return result;
    }

    public Map<String, Object> payOrder(String orderNo, PayOrderRequest request, AuthUser user) {
        Map<String, Object> order = commerceMapper.findOrderByOrderNo(orderNo);
        if (order == null) {
            throw new BizException(ErrorCode.BUSINESS_ERROR, "order not found");
        }
        Long owner = ((Number) order.get("userId")).longValue();
        if (user == null || !owner.equals(user.userId())) {
            throw new BizException(ErrorCode.FORBIDDEN, "order does not belong to current user");
        }

        String paymentRef = StringUtils.hasText(request.paymentRef())
                ? request.paymentRef()
                : "MOCKPAY-" + orderNo;
        int rows = commerceMapper.markOrderPaid(orderNo, paymentRef, LocalDateTime.now());
        if (rows != 1) {
            throw new BizException(ErrorCode.BUSINESS_ERROR, "order status invalid for payment");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderNo", orderNo);
        result.put("status", OrderStatus.PAID.name());
        result.put("paymentRef", paymentRef);
        return result;
    }

    public List<Map<String, Object>> listMyOrders(AuthUser user) {
        if (user == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "Unauthorized");
        }
        return commerceMapper.listOrdersByUser(user.userId());
    }

    private BigDecimal toDecimal(Object value) {
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        return new BigDecimal(String.valueOf(value));
    }

    private String generateOrderNo() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int suffix = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "OD-" + ts + "-" + suffix;
    }
}