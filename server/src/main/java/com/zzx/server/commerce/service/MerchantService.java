package com.zzx.server.commerce.service;

import com.zzx.server.commerce.dto.BatchCreateRequest;
import com.zzx.server.commerce.dto.BatchEventCreateRequest;
import com.zzx.server.commerce.dto.FruitCreateRequest;
import com.zzx.server.commerce.mapper.CommerceMapper;
import com.zzx.server.common.api.ErrorCode;
import com.zzx.server.common.exception.BizException;
import com.zzx.server.common.security.AuthUser;
import com.zzx.server.common.util.SignUtils;
import com.zzx.server.trace.gateway.ChainAnchorGateway;
import com.zzx.server.trace.mapper.AnchorMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MerchantService {

    private final CommerceMapper commerceMapper;
    private final ChainAnchorGateway chainAnchorGateway;
    private final AnchorMapper anchorMapper;
    private final String signKey;

    public MerchantService(CommerceMapper commerceMapper,
                           ChainAnchorGateway chainAnchorGateway,
                           AnchorMapper anchorMapper,
                           @Value("${trace.sign.key}") String signKey) {
        this.commerceMapper = commerceMapper;
        this.chainAnchorGateway = chainAnchorGateway;
        this.anchorMapper = anchorMapper;
        this.signKey = signKey;
    }

    public List<Map<String, Object>> listMerchantFruits(AuthUser user) {
        return commerceMapper.listMerchantFruits(resolveShopId(user));
    }

    public void createFruit(FruitCreateRequest request, AuthUser user) {
        commerceMapper.insertFruit(resolveShopId(user), request);
    }

    @Transactional
    public Map<String, Object> createBatch(BatchCreateRequest request, AuthUser user) {
        Long shopId = resolveShopId(user);
        Map<String, Object> fruit = commerceMapper.findFruitById(request.fruitId());
        if (fruit == null || !shopId.equals(((Number) fruit.get("shopId")).longValue())) {
            throw new BizException(ErrorCode.BUSINESS_ERROR, "fruit does not belong to merchant shop");
        }

        String traceId = StringUtils.hasText(request.traceId())
                ? request.traceId()
                : "TRACE-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();

        commerceMapper.insertBatch(shopId, traceId, request);
        Long batchId = commerceMapper.findBatchIdByBatchNo(request.batchNo());
        commerceMapper.insertInventory(batchId, request.quantity());

        String signature = SignUtils.traceSignature(signKey, traceId, request.batchNo());
        String payload = "{\"traceId\":\"" + traceId + "\",\"batchNo\":\"" + request.batchNo() + "\"}";
        commerceMapper.insertTraceCode(batchId, traceId, payload, signature);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("batchId", batchId);
        result.put("batchNo", request.batchNo());
        result.put("traceId", traceId);
        result.put("signature", signature);
        return result;
    }

    @Transactional
    public Map<String, Object> createBatchEvent(BatchEventCreateRequest request, AuthUser user) {
        Long shopId = resolveShopId(user);
        Map<String, Object> batch = commerceMapper.findBatchById(request.batchId());
        if (batch == null || !shopId.equals(((Number) batch.get("shopId")).longValue())) {
            throw new BizException(ErrorCode.BUSINESS_ERROR, "batch does not belong to merchant shop");
        }

        String payload = StringUtils.hasText(request.payloadJson()) ? request.payloadJson() : "{}";
        String eventHash = SignUtils.eventHash((String) batch.get("batchNo"), request.eventType(), request.eventTime(), payload);
        String anchorId = chainAnchorGateway.anchor(eventHash);

        commerceMapper.insertBatchEvent(
                request.batchId(),
                request.eventType(),
                request.eventTime(),
                request.location(),
                user.userId(),
                payload,
                eventHash,
                anchorId
        );

        Long eventId = commerceMapper.findBatchEventIdByHash(eventHash);
        anchorMapper.bindBatchEvent(anchorId, eventId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("eventHash", eventHash);
        result.put("anchorId", anchorId);
        return result;
    }

    private Long resolveShopId(AuthUser user) {
        if (user == null || user.shopId() == null) {
            throw new BizException(ErrorCode.FORBIDDEN, "merchant shop context not found");
        }
        return user.shopId();
    }
}