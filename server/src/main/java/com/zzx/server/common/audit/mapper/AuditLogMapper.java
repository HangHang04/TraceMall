package com.zzx.server.common.audit.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AuditLogMapper {

    @Insert("""
            INSERT INTO tm_audit_log(user_id, username, action, module, target_id, request_path, request_method, request_body, response_code, ip)
            VALUES (#{userId}, #{username}, #{action}, #{module}, #{targetId}, #{requestPath}, #{requestMethod}, #{requestBody}, #{responseCode}, #{ip})
            """)
    int insertAuditLog(@Param("userId") Long userId,
                       @Param("username") String username,
                       @Param("action") String action,
                       @Param("module") String module,
                       @Param("targetId") String targetId,
                       @Param("requestPath") String requestPath,
                       @Param("requestMethod") String requestMethod,
                       @Param("requestBody") String requestBody,
                       @Param("responseCode") String responseCode,
                       @Param("ip") String ip);

    @Select("""
            SELECT id,
                   user_id AS userId,
                   username,
                   action,
                   module,
                   target_id AS targetId,
                   request_path AS requestPath,
                   request_method AS requestMethod,
                   response_code AS responseCode,
                   ip,
                   created_at AS createdAt
            FROM tm_audit_log
            WHERE is_deleted = 0
            ORDER BY id DESC
            LIMIT #{size} OFFSET #{offset}
            """)
    List<Map<String, Object>> listAuditLogs(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(1) FROM tm_audit_log WHERE is_deleted = 0")
    long countAuditLogs();
}