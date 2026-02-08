package com.zzx.server.trace.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface AnchorMapper {

    @Insert("""
            INSERT INTO tm_chain_anchor(batch_event_id, event_hash, anchor_id, chain_type, anchor_status, anchored_at, verify_result, tx_hash)
            VALUES (NULL, #{eventHash}, #{anchorId}, #{chainType}, #{status}, #{anchoredAt}, #{verifyResult}, #{txHash})
            """)
    int insertAnchor(@Param("eventHash") String eventHash,
                     @Param("anchorId") String anchorId,
                     @Param("chainType") String chainType,
                     @Param("status") String status,
                     @Param("anchoredAt") LocalDateTime anchoredAt,
                     @Param("verifyResult") String verifyResult,
                     @Param("txHash") String txHash);

    @Update("UPDATE tm_chain_anchor SET batch_event_id = #{batchEventId} WHERE anchor_id = #{anchorId}")
    int bindBatchEvent(@Param("anchorId") String anchorId, @Param("batchEventId") Long batchEventId);

    @Select("""
            SELECT id,
                   event_hash AS eventHash,
                   anchor_id AS anchorId,
                   anchor_status AS anchorStatus,
                   verify_result AS verifyResult
            FROM tm_chain_anchor
            WHERE anchor_id = #{anchorId}
              AND event_hash = #{eventHash}
              AND is_deleted = 0
            LIMIT 1
            """)
    Map<String, Object> findByAnchorAndHash(@Param("anchorId") String anchorId, @Param("eventHash") String eventHash);
}