package com.zzx.server.trace.gateway;

import com.zzx.server.common.util.SignUtils;
import com.zzx.server.trace.mapper.AnchorMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
public class MockChainAnchorGateway implements ChainAnchorGateway {

    private final AnchorMapper anchorMapper;

    public MockChainAnchorGateway(AnchorMapper anchorMapper) {
        this.anchorMapper = anchorMapper;
    }

    @Override
    public String anchor(String eventHash) {
        String anchorId = "MOCK-ANCHOR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        String txHash = "0x" + SignUtils.sha256("tx:" + eventHash).substring(0, 40);
        anchorMapper.insertAnchor(eventHash, anchorId, "MOCK", "ANCHORED", LocalDateTime.now(), "MATCHED", txHash);
        return anchorId;
    }

    @Override
    public boolean verify(String anchorId, String eventHash) {
        Map<String, Object> anchor = anchorMapper.findByAnchorAndHash(anchorId, eventHash);
        return anchor != null && "ANCHORED".equals(anchor.get("anchorStatus"));
    }
}