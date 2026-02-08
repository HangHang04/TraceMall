package com.zzx.server.commerce.service;

import com.zzx.server.commerce.mapper.CommerceMapper;
import com.zzx.server.common.api.ErrorCode;
import com.zzx.server.common.exception.BizException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MallService {

    private final CommerceMapper commerceMapper;

    public MallService(CommerceMapper commerceMapper) {
        this.commerceMapper = commerceMapper;
    }

    public List<Map<String, Object>> listFruits() {
        return commerceMapper.listPublicFruits();
    }

    public Map<String, Object> getFruitDetail(Long fruitId) {
        Map<String, Object> fruit = commerceMapper.findFruitById(fruitId);
        if (fruit == null) {
            throw new BizException(ErrorCode.BUSINESS_ERROR, "fruit not found");
        }
        return fruit;
    }
}