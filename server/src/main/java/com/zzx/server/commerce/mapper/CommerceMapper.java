package com.zzx.server.commerce.mapper;

import com.zzx.server.commerce.dto.BatchCreateRequest;
import com.zzx.server.commerce.dto.FruitCreateRequest;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface CommerceMapper {

    @Select("""
            SELECT f.id,
                   f.fruit_name AS fruitName,
                   f.category,
                   f.origin,
                   f.unit,
                   f.unit_price AS unitPrice,
                   f.description,
                   s.shop_name AS shopName
            FROM tm_fruit f
            JOIN tm_shop s ON s.id = f.shop_id
            WHERE f.status = 'ON_SALE'
              AND f.is_deleted = 0
            ORDER BY f.id DESC
            """)
    List<Map<String, Object>> listPublicFruits();

    @Select("""
            SELECT f.id,
                   f.fruit_name AS fruitName,
                   f.category,
                   f.origin,
                   f.unit,
                   f.unit_price AS unitPrice,
                   f.description,
                   s.shop_name AS shopName,
                   s.id AS shopId
            FROM tm_fruit f
            JOIN tm_shop s ON s.id = f.shop_id
            WHERE f.id = #{fruitId}
              AND f.is_deleted = 0
            """)
    Map<String, Object> findFruitById(Long fruitId);

    @Select("""
            SELECT id
            FROM tm_shop
            WHERE owner_user_id = #{userId}
              AND is_deleted = 0
            LIMIT 1
            """)
    Long findShopIdByOwnerId(Long userId);

    @Select("""
            SELECT f.id,
                   f.fruit_name AS fruitName,
                   f.category,
                   f.origin,
                   f.unit,
                   f.unit_price AS unitPrice,
                   f.status
            FROM tm_fruit f
            WHERE f.shop_id = #{shopId}
              AND f.is_deleted = 0
            ORDER BY f.id DESC
            """)
    List<Map<String, Object>> listMerchantFruits(Long shopId);

    @Insert("""
            INSERT INTO tm_fruit(shop_id, fruit_name, category, origin, unit, unit_price, description, status)
            VALUES (#{shopId}, #{req.fruitName}, #{req.category}, #{req.origin},
                    COALESCE(#{req.unit}, 'kg'), #{req.unitPrice}, #{req.description}, 'ON_SALE')
            """)
    int insertFruit(@Param("shopId") Long shopId, @Param("req") FruitCreateRequest req);

    @Insert("""
            INSERT INTO tm_batch(fruit_id, shop_id, batch_no, trace_id, harvest_date, expire_date, quantity, remaining_quantity, status)
            VALUES (#{req.fruitId}, #{shopId}, #{req.batchNo}, #{traceId}, #{req.harvestDate}, #{req.expireDate},
                    #{req.quantity}, #{req.quantity}, 'IN_STOCK')
            """)
    int insertBatch(@Param("shopId") Long shopId, @Param("traceId") String traceId, @Param("req") BatchCreateRequest req);

    @Select("SELECT id FROM tm_batch WHERE batch_no = #{batchNo} AND is_deleted = 0 LIMIT 1")
    Long findBatchIdByBatchNo(String batchNo);

    @Select("""
            SELECT id,
                   shop_id AS shopId,
                   batch_no AS batchNo,
                   trace_id AS traceId,
                   fruit_id AS fruitId,
                   remaining_quantity AS remainingQuantity,
                   quantity,
                   status
            FROM tm_batch
            WHERE id = #{batchId}
              AND is_deleted = 0
            """)
    Map<String, Object> findBatchById(Long batchId);

    @Select("""
            SELECT b.id,
                   b.batch_no AS batchNo,
                   b.trace_id AS traceId,
                   b.shop_id AS shopId,
                   b.remaining_quantity AS remainingQuantity,
                   f.id AS fruitId,
                   f.fruit_name AS fruitName,
                   f.unit_price AS unitPrice,
                   f.unit
            FROM tm_batch b
            JOIN tm_fruit f ON f.id = b.fruit_id
            WHERE b.id = #{batchId}
              AND b.is_deleted = 0
              AND f.is_deleted = 0
            """)
    Map<String, Object> findBatchOrderSnapshot(Long batchId);

    @Insert("INSERT INTO tm_inventory(batch_id, stock_qty, locked_qty) VALUES (#{batchId}, #{stockQty}, 0)")
    int insertInventory(@Param("batchId") Long batchId, @Param("stockQty") BigDecimal stockQty);

    @Insert("""
            INSERT INTO tm_trace_code(batch_id, trace_id, qr_payload, signature, issued_at, expires_at, status)
            VALUES (#{batchId}, #{traceId}, #{payload}, #{signature}, NOW(), DATE_ADD(NOW(), INTERVAL 180 DAY), 'ACTIVE')
            """)
    int insertTraceCode(@Param("batchId") Long batchId,
                        @Param("traceId") String traceId,
                        @Param("payload") String payload,
                        @Param("signature") String signature);

    @Insert("""
            INSERT INTO tm_batch_event(batch_id, event_type, event_time, location, operator_id, payload_json, event_hash, anchor_id)
            VALUES (#{batchId}, #{eventType}, #{eventTime}, #{location}, #{operatorId}, CAST(#{payloadJson} AS JSON), #{eventHash}, #{anchorId})
            """)
    int insertBatchEvent(@Param("batchId") Long batchId,
                         @Param("eventType") String eventType,
                         @Param("eventTime") LocalDateTime eventTime,
                         @Param("location") String location,
                         @Param("operatorId") Long operatorId,
                         @Param("payloadJson") String payloadJson,
                         @Param("eventHash") String eventHash,
                         @Param("anchorId") String anchorId);

    @Select("SELECT id FROM tm_batch_event WHERE event_hash = #{eventHash} ORDER BY id DESC LIMIT 1")
    Long findBatchEventIdByHash(String eventHash);

    @Select("""
            SELECT event_type AS eventType,
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

    @Update("""
            UPDATE tm_inventory
            SET stock_qty = stock_qty - #{quantity},
                updated_at = CURRENT_TIMESTAMP
            WHERE batch_id = #{batchId}
              AND stock_qty >= #{quantity}
            """)
    int decreaseInventory(@Param("batchId") Long batchId, @Param("quantity") BigDecimal quantity);

    @Update("""
            UPDATE tm_batch
            SET remaining_quantity = remaining_quantity - #{quantity},
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{batchId}
              AND remaining_quantity >= #{quantity}
            """)
    int decreaseBatchRemaining(@Param("batchId") Long batchId, @Param("quantity") BigDecimal quantity);

    @Insert("""
            INSERT INTO tm_order(user_id, order_no, status, total_amount, payable_amount, shipping_address, remark)
            VALUES (#{userId}, #{orderNo}, #{status}, #{totalAmount}, #{payableAmount}, #{shippingAddress}, #{remark})
            """)
    int insertOrder(@Param("userId") Long userId,
                    @Param("orderNo") String orderNo,
                    @Param("status") String status,
                    @Param("totalAmount") BigDecimal totalAmount,
                    @Param("payableAmount") BigDecimal payableAmount,
                    @Param("shippingAddress") String shippingAddress,
                    @Param("remark") String remark);

    @Select("SELECT id FROM tm_order WHERE order_no = #{orderNo} AND is_deleted = 0 LIMIT 1")
    Long findOrderIdByOrderNo(String orderNo);

    @Insert("""
            INSERT INTO tm_order_item(order_id, fruit_id, batch_id, quantity, unit_price, total_price)
            VALUES (#{orderId}, #{fruitId}, #{batchId}, #{quantity}, #{unitPrice}, #{totalPrice})
            """)
    int insertOrderItem(@Param("orderId") Long orderId,
                        @Param("fruitId") Long fruitId,
                        @Param("batchId") Long batchId,
                        @Param("quantity") BigDecimal quantity,
                        @Param("unitPrice") BigDecimal unitPrice,
                        @Param("totalPrice") BigDecimal totalPrice);

    @Select("""
            SELECT o.id,
                   o.order_no AS orderNo,
                   o.status,
                   o.total_amount AS totalAmount,
                   o.payable_amount AS payableAmount,
                   o.paid_at AS paidAt,
                   o.payment_ref AS paymentRef,
                   o.shipping_address AS shippingAddress,
                   o.remark,
                   o.created_at AS createdAt
            FROM tm_order o
            WHERE o.user_id = #{userId}
              AND o.is_deleted = 0
            ORDER BY o.id DESC
            """)
    List<Map<String, Object>> listOrdersByUser(Long userId);

    @Select("""
            SELECT id,
                   user_id AS userId,
                   order_no AS orderNo,
                   status,
                   total_amount AS totalAmount,
                   payable_amount AS payableAmount,
                   paid_at AS paidAt,
                   payment_ref AS paymentRef
            FROM tm_order
            WHERE order_no = #{orderNo}
              AND is_deleted = 0
            LIMIT 1
            """)
    Map<String, Object> findOrderByOrderNo(String orderNo);

    @Update("""
            UPDATE tm_order
            SET status = 'PAID',
                paid_at = #{paidAt},
                payment_ref = #{paymentRef},
                updated_at = CURRENT_TIMESTAMP
            WHERE order_no = #{orderNo}
              AND status IN ('CREATED', 'PENDING_PAYMENT')
              AND is_deleted = 0
            """)
    int markOrderPaid(@Param("orderNo") String orderNo,
                      @Param("paymentRef") String paymentRef,
                      @Param("paidAt") LocalDateTime paidAt);

    @Update("""
            UPDATE tm_order
            SET total_amount = #{totalAmount},
                payable_amount = #{payableAmount},
                updated_at = CURRENT_TIMESTAMP
            WHERE order_no = #{orderNo}
              AND is_deleted = 0
            """)
    int updateOrderAmount(@Param("orderNo") String orderNo,
                          @Param("totalAmount") BigDecimal totalAmount,
                          @Param("payableAmount") BigDecimal payableAmount);

    @Update("""
            UPDATE tm_order
            SET status = #{status},
                updated_at = CURRENT_TIMESTAMP
            WHERE order_no = #{orderNo}
              AND is_deleted = 0
            """)
    int updateOrderStatus(@Param("orderNo") String orderNo, @Param("status") String status);
}
