package com.zzx.server.trace.service;

import com.zzx.server.common.util.SignUtils;
import com.zzx.server.trace.dto.VerifyResult;
import com.zzx.server.trace.dto.VerifyTraceRequest;
import com.zzx.server.trace.gateway.ChainAnchorGateway;
import com.zzx.server.trace.mapper.TraceMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

class TraceServiceTests {

    @Test
    void verifyShouldFailWhenSignatureMismatch() {
        TraceMapper mapper = Mockito.mock(TraceMapper.class);
        ChainAnchorGateway gateway = Mockito.mock(ChainAnchorGateway.class);
        TraceService service = new TraceService(mapper, gateway, "TRACE_SIGN_KEY_2026");

        Map<String, Object> summary = new HashMap<>();
        summary.put("batchId", 1L);
        summary.put("batchNo", "BATCH-001");
        summary.put("signature", SignUtils.traceSignature("TRACE_SIGN_KEY_2026", "TRACE-TEST-001", "BATCH-001"));
        Mockito.when(mapper.findTraceSummary("TRACE-TEST-001")).thenReturn(summary);

        VerifyResult result = service.verify(new VerifyTraceRequest("TRACE-TEST-001", "bad-sign", "四川-成都", "dev-a", "10.0.0.1"));

        Assertions.assertEquals("FAIL", result.status());
        Assertions.assertEquals("HIGH", result.riskLevel());
        Mockito.verify(mapper).insertRiskAlert(Mockito.eq("TRACE-TEST-001"), Mockito.eq(1L), Mockito.eq("SIGNATURE_MISMATCH"), Mockito.eq("HIGH"), Mockito.anyString());
    }

    @Test
    void verifyShouldPassWhenAnchorMatches() {
        TraceMapper mapper = Mockito.mock(TraceMapper.class);
        ChainAnchorGateway gateway = Mockito.mock(ChainAnchorGateway.class);
        TraceService service = new TraceService(mapper, gateway, "TRACE_SIGN_KEY_2026");

        Map<String, Object> summary = new HashMap<>();
        summary.put("batchId", 2L);
        summary.put("batchNo", "BATCH-002");
        summary.put("signature", SignUtils.traceSignature("TRACE_SIGN_KEY_2026", "TRACE-TEST-002", "BATCH-002"));
        Mockito.when(mapper.findTraceSummary("TRACE-TEST-002")).thenReturn(summary);

        Map<String, Object> qualityEvent = new HashMap<>();
        qualityEvent.put("anchorId", "MOCK-ANCHOR-001");
        qualityEvent.put("eventHash", "hash001");
        Mockito.when(mapper.findLatestQualityEvent(2L)).thenReturn(qualityEvent);
        Mockito.when(gateway.verify("MOCK-ANCHOR-001", "hash001")).thenReturn(true);
        Mockito.when(mapper.countRecentScans("TRACE-TEST-002")).thenReturn(1L);
        Mockito.when(mapper.countRecentDistinctGeo("TRACE-TEST-002")).thenReturn(1L);

        String sign = SignUtils.traceSignature("TRACE_SIGN_KEY_2026", "TRACE-TEST-002", "BATCH-002");
        VerifyResult result = service.verify(new VerifyTraceRequest("TRACE-TEST-002", sign, "四川-成都", "dev-b", "10.0.0.2"));

        Assertions.assertEquals("PASS", result.status());
        Assertions.assertTrue(result.anchorMatched());
    }
}