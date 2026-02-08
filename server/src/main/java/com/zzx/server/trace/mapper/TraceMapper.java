package com.zzx.server.trace.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface TraceMapper {

    @Select("""
            SELECT t.trace_id AS traceId,
                   t.signature,
                   t.qr_payload AS qrPayload,
                   t.issued_at AS issuedAt,
                   b.id AS batchId,
                   b.batch_no AS batchNo,
                   b.status AS batchStatus,
                   b.harvest_date AS harvestDate,
                   b.expire_date AS expireDate,
                   f.id AS fruitId,
                   f.fruit_name AS fruitName,
                   f.category,
                   f.origin,
                   f.unit,
                   f.unit_price AS unitPrice,
                   s.shop_name AS shopName
            FROM tm_trace_code t
            JOIN tm_batch b ON b.id = t.batch_id
            JOIN tm_fruit f ON f.id = b.fruit_id
            JOIN tm_shop s ON s.id = b.shop_id
            WHERE t.trace_id = #{traceId}
              AND t.is_deleted = 0
              AND b.is_deleted = 0
              AND f.is_deleted = 0
            LIMIT 1
            """)
    Map<String, Object> findTraceSummary(String traceId);

    @Select("""
            SELECT id,
                   event_type AS eventType,
                   event_time AS eventTime,
                   location,
                   payload_json AS payloadJson,
                   event_hash AS eventHash,
                   anchor_id AS anchorId
            FROM tm_batch_event
            WHERE batch_id = #{batchId}
              AND is_deleted = 0
            ORDER BY event_time ASC
            """)
    List<Map<String, Object>> listBatchEvents(Long batchId);

    @Select("""
            SELECT id,
                   event_hash AS eventHash,
                   anchor_id AS anchorId
            FROM tm_batch_event
            WHERE batch_id = #{batchId}
              AND event_type = 'QUALITY_CHECK'
              AND is_deleted = 0
            ORDER BY event_time DESC
            LIMIT 1
            """)
    Map<String, Object> findLatestQualityEvent(Long batchId);

    @Insert("""
            INSERT INTO tm_trace_scan_log(trace_id, batch_id, scan_time, ip, geo, device_fingerprint, result_status, result_reason)
            VALUES (#{traceId}, #{batchId}, NOW(), #{ip}, #{geo}, #{deviceFingerprint}, #{resultStatus}, #{resultReason})
            """)
    int insertScanLog(@Param("traceId") String traceId,
                      @Param("batchId") Long batchId,
                      @Param("ip") String ip,
                      @Param("geo") String geo,
                      @Param("deviceFingerprint") String deviceFingerprint,
                      @Param("resultStatus") String resultStatus,
                      @Param("resultReason") String resultReason);

    @Select("""
            SELECT COUNT(1)
            FROM tm_trace_scan_log
            WHERE trace_id = #{traceId}
              AND scan_time >= DATE_SUB(NOW(), INTERVAL 5 MINUTE)
              AND is_deleted = 0
            """)
    long countRecentScans(String traceId);

    @Select("""
            SELECT COUNT(DISTINCT geo)
            FROM tm_trace_scan_log
            WHERE trace_id = #{traceId}
              AND geo IS NOT NULL
              AND scan_time >= DATE_SUB(NOW(), INTERVAL 5 MINUTE)
              AND is_deleted = 0
            """)
    long countRecentDistinctGeo(String traceId);

    @Insert("""
            INSERT INTO tm_risk_alert(trace_id, batch_id, risk_type, risk_level, description, status, detected_at)
            VALUES (#{traceId}, #{batchId}, #{riskType}, #{riskLevel}, #{description}, 'OPEN', NOW())
            """)
    int insertRiskAlert(@Param("traceId") String traceId,
                        @Param("batchId") Long batchId,
                        @Param("riskType") String riskType,
                        @Param("riskLevel") String riskLevel,
                        @Param("description") String description);
}