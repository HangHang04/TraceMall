package com.zzx.server.regulator.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface RegulatorMapper {

    @Select("""
            SELECT id,
                   trace_id AS traceId,
                   batch_id AS batchId,
                   risk_type AS riskType,
                   risk_level AS riskLevel,
                   description,
                   status,
                   detected_at AS detectedAt,
                   resolved_at AS resolvedAt
            FROM tm_risk_alert
            WHERE is_deleted = 0
            ORDER BY id DESC
            LIMIT #{size} OFFSET #{offset}
            """)
    List<Map<String, Object>> listRiskAlerts(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(1) FROM tm_risk_alert WHERE is_deleted = 0")
    long countRiskAlerts();
}