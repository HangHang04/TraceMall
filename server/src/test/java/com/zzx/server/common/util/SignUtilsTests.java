package com.zzx.server.common.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class SignUtilsTests {

    @Test
    void traceSignatureShouldBeStable() {
        String sign1 = SignUtils.traceSignature("k", "TRACE-001", "BATCH-001");
        String sign2 = SignUtils.traceSignature("k", "TRACE-001", "BATCH-001");
        Assertions.assertEquals(sign1, sign2);
        Assertions.assertEquals(64, sign1.length());
    }

    @Test
    void eventHashShouldChangeWhenPayloadChanges() {
        String hash1 = SignUtils.eventHash("BATCH-001", "QC", LocalDateTime.of(2026, 2, 8, 10, 0), "{}");
        String hash2 = SignUtils.eventHash("BATCH-001", "QC", LocalDateTime.of(2026, 2, 8, 10, 0), "{\"x\":1}");
        Assertions.assertNotEquals(hash1, hash2);
    }
}