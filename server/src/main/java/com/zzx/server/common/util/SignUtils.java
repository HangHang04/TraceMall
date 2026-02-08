package com.zzx.server.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;

public final class SignUtils {

    private SignUtils() {
    }

    public static String traceSignature(String signKey, String traceId, String batchNo) {
        return sha256(signKey + "|" + traceId + "|" + batchNo).substring(0, 64);
    }

    public static String eventHash(String batchNo, String eventType, LocalDateTime eventTime, String payload) {
        return sha256(batchNo + "|" + eventType + "|" + eventTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "|" + payload);
    }

    public static String sha256(String source) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(source.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}