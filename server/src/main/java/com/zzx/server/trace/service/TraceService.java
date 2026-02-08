package com.zzx.server.trace.service;

import com.zzx.server.common.api.ErrorCode;
import com.zzx.server.common.exception.BizException;
import com.zzx.server.common.util.SignUtils;
import com.zzx.server.trace.dto.VerifyResult;
import com.zzx.server.trace.dto.VerifyTraceRequest;
import com.zzx.server.trace.gateway.ChainAnchorGateway;
import com.zzx.server.trace.mapper.TraceMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class TraceService {

    private static final Pattern TRACE_PATTERN = Pattern.compile("^TRACE-[A-Z0-9-]{4,64}$");
    private final TraceMapper traceMapper;
    private final ChainAnchorGateway chainAnchorGateway;
    private final String signKey;

    public TraceService(TraceMapper traceMapper,
                        ChainAnchorGateway chainAnchorGateway,
                        @Value("${trace.sign.key}") String signKey) {
        this.traceMapper = traceMapper;
        this.chainAnchorGateway = chainAnchorGateway;
        this.signKey = signKey;
    }

    public Map<String, Object> traceDetail(String traceId) {
        Map<String, Object> summary = traceMapper.findTraceSummary(traceId);
        if (summary == null) {
            throw new BizException(ErrorCode.BUSINESS_ERROR, "trace code not found");
        }
        Long batchId = ((Number) summary.get("batchId")).longValue();
        List<Map<String, Object>> events = traceMapper.listBatchEvents(batchId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("summary", summary);
        result.put("events", events);
        return result;
    }

    public VerifyResult verify(VerifyTraceRequest request) {
        String traceId = request.traceId();
        if (!TRACE_PATTERN.matcher(traceId).matches()) {
            traceMapper.insertScanLog(traceId, null, normalizeIp(request.ip()), request.geo(), request.deviceFingerprint(), "FAIL", "traceId format invalid");
            return new VerifyResult("FAIL", "traceId format invalid", "HIGH", null, false);
        }

        Map<String, Object> summary = traceMapper.findTraceSummary(traceId);
        if (summary == null) {
            traceMapper.insertScanLog(traceId, null, normalizeIp(request.ip()), request.geo(), request.deviceFingerprint(), "FAIL", "trace code not found");
            return new VerifyResult("FAIL", "trace code not found", "HIGH", null, false);
        }

        Long batchId = ((Number) summary.get("batchId")).longValue();
        String batchNo = String.valueOf(summary.get("batchNo"));
        String storedSignature = String.valueOf(summary.get("signature"));
        String expectedSignature = SignUtils.traceSignature(signKey, traceId, batchNo);
        boolean signatureMatched = StringUtils.hasText(request.signature())
                && request.signature().equals(storedSignature)
                && storedSignature.equals(expectedSignature);

        if (!signatureMatched) {
            traceMapper.insertScanLog(traceId, batchId, normalizeIp(request.ip()), request.geo(), request.deviceFingerprint(), "FAIL", "signature mismatch");
            traceMapper.insertRiskAlert(traceId, batchId, "SIGNATURE_MISMATCH", "HIGH", "二维码签名校验失败，疑似篡改");
            return new VerifyResult("FAIL", "signature mismatch", "HIGH", summary, false);
        }

        Map<String, Object> qualityEvent = traceMapper.findLatestQualityEvent(batchId);
        boolean anchorMatched = false;
        if (qualityEvent != null && qualityEvent.get("anchorId") != null) {
            anchorMatched = chainAnchorGateway.verify(
                    String.valueOf(qualityEvent.get("anchorId")),
                    String.valueOf(qualityEvent.get("eventHash"))
            );
        }

        long recentCount = traceMapper.countRecentScans(traceId);
        long recentGeoCount = traceMapper.countRecentDistinctGeo(traceId);
        boolean highRisk = recentCount >= 3 && (recentGeoCount >= 2 || StringUtils.hasText(request.geo()));

        String status = "PASS";
        String reason = "verify passed";
        String riskLevel = "LOW";

        if (!anchorMatched) {
            status = "SUSPECT";
            reason = "anchor mismatch";
            riskLevel = "MODERATE";
            traceMapper.insertRiskAlert(traceId, batchId, "ANCHOR_MISMATCH", "MODERATE", "链上锚点校验未通过，疑似篡改");
        }
        if (highRisk) {
            status = "HIGH_RISK";
            reason = "frequent scan anomaly";
            riskLevel = "HIGH";
            traceMapper.insertRiskAlert(traceId, batchId, "FREQUENT_SCAN", "HIGH", "短时高频/异地扫描触发风控");
        }

        traceMapper.insertScanLog(traceId, batchId, normalizeIp(request.ip()), request.geo(), request.deviceFingerprint(), status, reason);
        return new VerifyResult(status, reason, riskLevel, summary, anchorMatched);
    }

    private String normalizeIp(String ip) {
        return StringUtils.hasText(ip) ? ip : "0.0.0.0";
    }
}