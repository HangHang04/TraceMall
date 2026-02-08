INSERT INTO tm_role (role_code, role_name, description) VALUES
('CONSUMER', '消费者', '购买水果并查询溯源'),
('MERCHANT', '商家', '管理水果、批次与库存'),
('REGULATOR', '监管员', '监管查询与风险审计');

INSERT INTO tm_user (username, password, nickname, phone, email, status) VALUES
('consumer01', '{noop}123456', '小李', '13800000001', 'consumer01@tracemall.local', 'ACTIVE'),
('consumer02', '{noop}123456', '小王', '13800000002', 'consumer02@tracemall.local', 'ACTIVE'),
('merchant01', '{noop}123456', '果园A负责人', '13800000011', 'merchant01@tracemall.local', 'ACTIVE'),
('merchant02', '{noop}123456', '果园B负责人', '13800000012', 'merchant02@tracemall.local', 'ACTIVE'),
('regulator01', '{noop}123456', '监管员张', '13800000021', 'regulator01@tracemall.local', 'ACTIVE');

INSERT INTO tm_user_role (user_id, role_id) VALUES
(1, 1),
(2, 1),
(3, 2),
(4, 2),
(5, 3);

INSERT INTO tm_shop (owner_user_id, shop_name, license_no, contact_phone, address, status) VALUES
(3, '川西鲜果旗舰店', 'LIC-TRACE-0001', '028-11110001', '四川省成都市高新区天府大道 1000 号', 'ACTIVE'),
(4, '岭南优果直营店', 'LIC-TRACE-0002', '020-22220002', '广东省广州市天河区体育西路 200 号', 'ACTIVE');

INSERT INTO tm_fruit (shop_id, fruit_name, category, origin, unit, unit_price, image_url, description, status) VALUES
(1, '红富士苹果', '苹果', '四川阿坝', 'kg', 18.80, NULL, '脆甜口感，糖酸平衡', 'ON_SALE'),
(1, '爱媛橙', '柑橘', '四川眉山', 'kg', 15.60, NULL, '果汁丰富，低酸高甜', 'ON_SALE'),
(1, '阳光玫瑰葡萄', '葡萄', '云南红河', 'kg', 29.90, NULL, '粒大皮薄，香气浓郁', 'ON_SALE'),
(1, '红心猕猴桃', '猕猴桃', '四川蒲江', 'kg', 22.00, NULL, '维C含量高，果肉细腻', 'ON_SALE'),
(1, '蓝莓', '浆果', '云南大理', 'kg', 45.00, NULL, '新鲜采摘，冷链直达', 'ON_SALE'),
(2, '库尔勒香梨', '梨', '新疆库尔勒', 'kg', 19.50, NULL, '果香清雅，水分足', 'ON_SALE'),
(2, '广西沃柑', '柑橘', '广西南宁', 'kg', 13.90, NULL, '果肉细嫩，甜度稳定', 'ON_SALE'),
(2, '青提', '葡萄', '广东清远', 'kg', 24.80, NULL, '爽脆多汁，轻甜清爽', 'ON_SALE'),
(2, '草莓', '浆果', '广东惠州', 'kg', 36.50, NULL, '鲜红饱满，香甜浓郁', 'ON_SALE'),
(2, '香蕉', '热带水果', '海南乐东', 'kg', 9.90, NULL, '软糯香甜，成熟度高', 'ON_SALE');

INSERT INTO tm_batch (fruit_id, shop_id, batch_no, trace_id, harvest_date, expire_date, quantity, remaining_quantity, status) VALUES
(1, 1, 'BATCH-APPLE-001', 'TRACE-APPLE-001', '2026-01-15', '2026-03-01', 500.000, 500.000, 'IN_STOCK'),
(1, 1, 'BATCH-APPLE-002', 'TRACE-APPLE-002', '2026-01-28', '2026-03-10', 460.000, 460.000, 'IN_STOCK'),
(2, 1, 'BATCH-ORANGE-001', 'TRACE-ORANGE-001', '2026-01-16', '2026-02-28', 520.000, 520.000, 'IN_STOCK'),
(2, 1, 'BATCH-ORANGE-002', 'TRACE-ORANGE-002', '2026-01-30', '2026-03-12', 510.000, 510.000, 'IN_STOCK'),
(3, 1, 'BATCH-GRAPE-001', 'TRACE-GRAPE-001', '2026-01-12', '2026-02-25', 380.000, 380.000, 'IN_STOCK'),
(3, 1, 'BATCH-GRAPE-002', 'TRACE-GRAPE-002', '2026-01-26', '2026-03-05', 390.000, 390.000, 'IN_STOCK'),
(4, 1, 'BATCH-KIWI-001', 'TRACE-KIWI-001', '2026-01-14', '2026-03-03', 430.000, 430.000, 'IN_STOCK'),
(4, 1, 'BATCH-KIWI-002', 'TRACE-KIWI-002', '2026-01-27', '2026-03-12', 420.000, 420.000, 'IN_STOCK'),
(5, 1, 'BATCH-BLUEBERRY-001', 'TRACE-BLUEBERRY-001', '2026-01-13', '2026-02-20', 260.000, 260.000, 'IN_STOCK'),
(5, 1, 'BATCH-BLUEBERRY-002', 'TRACE-BLUEBERRY-002', '2026-01-25', '2026-03-01', 255.000, 255.000, 'IN_STOCK'),
(6, 2, 'BATCH-PEAR-001', 'TRACE-PEAR-001', '2026-01-15', '2026-03-08', 480.000, 480.000, 'IN_STOCK'),
(6, 2, 'BATCH-PEAR-002', 'TRACE-PEAR-002', '2026-01-29', '2026-03-18', 470.000, 470.000, 'IN_STOCK'),
(7, 2, 'BATCH-WOGAN-001', 'TRACE-WOGAN-001', '2026-01-17', '2026-03-05', 530.000, 530.000, 'IN_STOCK'),
(7, 2, 'BATCH-WOGAN-002', 'TRACE-WOGAN-002', '2026-02-01', '2026-03-20', 540.000, 540.000, 'IN_STOCK'),
(8, 2, 'BATCH-GREEN-GRAPE-001', 'TRACE-GREEN-GRAPE-001', '2026-01-16', '2026-03-02', 410.000, 410.000, 'IN_STOCK'),
(8, 2, 'BATCH-GREEN-GRAPE-002', 'TRACE-GREEN-GRAPE-002', '2026-01-31', '2026-03-15', 405.000, 405.000, 'IN_STOCK'),
(9, 2, 'BATCH-STRAWBERRY-001', 'TRACE-STRAWBERRY-001', '2026-01-14', '2026-02-18', 300.000, 300.000, 'IN_STOCK'),
(9, 2, 'BATCH-STRAWBERRY-002', 'TRACE-STRAWBERRY-002', '2026-01-26', '2026-02-28', 310.000, 310.000, 'IN_STOCK'),
(10, 2, 'BATCH-BANANA-001', 'TRACE-BANANA-001', '2026-01-18', '2026-02-22', 600.000, 600.000, 'IN_STOCK'),
(10, 2, 'BATCH-BANANA-002', 'TRACE-BANANA-002', '2026-02-02', '2026-03-05', 620.000, 620.000, 'IN_STOCK');

INSERT INTO tm_inventory (batch_id, stock_qty, locked_qty)
SELECT id, remaining_quantity, 0.000
FROM tm_batch;

INSERT INTO tm_batch_event (batch_id, event_type, event_time, location, operator_id, payload_json, event_hash)
SELECT b.id,
       'HARVEST',
       DATE_ADD('2026-01-10 08:00:00', INTERVAL b.id DAY),
       CONCAT('产地仓-', b.shop_id),
       CASE WHEN b.shop_id = 1 THEN 3 ELSE 4 END,
       JSON_OBJECT('temperature', '8-12C', 'humidity', '45%-60%', 'stage', 'harvest'),
       SHA2(CONCAT('HARVEST-', b.trace_id, '-', b.batch_no), 256)
FROM tm_batch b;

INSERT INTO tm_batch_event (batch_id, event_type, event_time, location, operator_id, payload_json, event_hash)
SELECT b.id,
       'QUALITY_CHECK',
       DATE_ADD('2026-01-11 13:30:00', INTERVAL b.id DAY),
       CONCAT('质检中心-', b.shop_id),
       CASE WHEN b.shop_id = 1 THEN 3 ELSE 4 END,
       JSON_OBJECT('result', 'PASS', 'pesticide', 'qualified', 'appearance', 'qualified'),
       SHA2(CONCAT('QC-', b.trace_id, '-', b.batch_no), 256)
FROM tm_batch b;

INSERT INTO tm_trace_code (batch_id, trace_id, qr_payload, signature, issued_at, expires_at, status)
SELECT b.id,
       b.trace_id,
       CONCAT('{"traceId":"', b.trace_id, '","batchNo":"', b.batch_no, '","issuedAt":"2026-02-01T00:00:00"}'),
       SUBSTRING(SHA2(CONCAT('TRACE_SIGN_KEY_', b.trace_id, '_', b.batch_no), 256), 1, 64),
       '2026-02-01 00:00:00',
       '2026-06-30 23:59:59',
       'ACTIVE'
FROM tm_batch b;

INSERT INTO tm_order (user_id, order_no, status, total_amount, payable_amount, paid_at, payment_ref, shipping_address, remark) VALUES
(1, 'OD-202602-0001', 'PAID', 83.50, 83.50, '2026-02-05 10:20:00', 'MOCKPAY-0001', '成都市武侯区人民南路 1 号', '尽快发货'),
(1, 'OD-202602-0002', 'SHIPPED', 49.80, 49.80, '2026-02-06 12:00:00', 'MOCKPAY-0002', '成都市高新区天府三街 200 号', NULL),
(2, 'OD-202602-0003', 'PENDING_PAYMENT', 65.70, 65.70, NULL, NULL, '广州市天河区华夏路 10 号', '周末送达'),
(2, 'OD-202602-0004', 'CREATED', 36.50, 36.50, NULL, NULL, '广州市海珠区新港中路 11 号', NULL),
(1, 'OD-202602-0005', 'COMPLETED', 105.20, 105.20, '2026-02-03 09:40:00', 'MOCKPAY-0005', '成都市锦江区春熙路 8 号', '已签收');

INSERT INTO tm_order_item (order_id, fruit_id, batch_id, quantity, unit_price, total_price) VALUES
(1, 1, 1, 2.000, 18.80, 37.60),
(1, 2, 3, 2.000, 15.60, 31.20),
(1, 10, 19, 1.500, 9.90, 14.70),
(2, 3, 5, 1.000, 29.90, 29.90),
(2, 7, 13, 1.000, 13.90, 13.90),
(2, 8, 15, 0.250, 24.80, 6.00),
(3, 4, 7, 1.500, 22.00, 33.00),
(3, 6, 11, 1.000, 19.50, 19.50),
(3, 9, 17, 0.362, 36.50, 13.20),
(4, 9, 18, 1.000, 36.50, 36.50),
(5, 1, 2, 1.500, 18.80, 28.20),
(5, 5, 10, 1.000, 45.00, 45.00),
(5, 6, 12, 1.641, 19.50, 32.00);

UPDATE tm_batch b
JOIN (
    SELECT batch_id, SUM(quantity) used_qty
    FROM tm_order_item
    GROUP BY batch_id
) oi ON b.id = oi.batch_id
SET b.remaining_quantity = GREATEST(b.quantity - oi.used_qty, 0);

UPDATE tm_inventory i
JOIN tm_batch b ON i.batch_id = b.id
SET i.stock_qty = b.remaining_quantity;

INSERT INTO tm_chain_anchor (batch_event_id, event_hash, anchor_id, chain_type, anchor_status, anchored_at, verify_result, tx_hash)
SELECT e.id,
       e.event_hash,
       CONCAT('MOCK-ANCHOR-', LPAD(e.id, 6, '0')),
       'MOCK',
       'ANCHORED',
       DATE_ADD(e.event_time, INTERVAL 5 MINUTE),
       'MATCHED',
       CONCAT('0x', SUBSTRING(SHA2(CONCAT('TX', e.event_hash), 256), 1, 40))
FROM tm_batch_event e
WHERE e.event_type = 'QUALITY_CHECK';

UPDATE tm_batch_event e
JOIN tm_chain_anchor a ON a.batch_event_id = e.id
SET e.anchor_id = a.anchor_id;

INSERT INTO tm_trace_scan_log (trace_id, batch_id, scan_time, ip, geo, device_fingerprint, result_status, result_reason) VALUES
('TRACE-APPLE-001', 1, '2026-02-06 09:00:00', '10.20.1.10', '四川-成都', 'dev-ios-a1', 'PASS', '校验通过'),
('TRACE-APPLE-001', 1, '2026-02-06 09:01:00', '10.20.1.10', '四川-成都', 'dev-ios-a1', 'PASS', '校验通过'),
('TRACE-APPLE-001', 1, '2026-02-06 09:02:00', '10.20.1.10', '四川-成都', 'dev-ios-a1', 'PASS', '校验通过'),
('TRACE-APPLE-001', 1, '2026-02-06 09:03:00', '183.32.10.1', '广东-深圳', 'dev-android-b2', 'SUSPECT', '短时异地重复扫描'),
('TRACE-WOGAN-001', 13, '2026-02-07 11:00:00', '10.30.8.5', '广东-广州', 'dev-web-c3', 'PASS', '校验通过'),
('TRACE-STRAWBERRY-002', 18, '2026-02-07 12:30:00', '10.30.8.8', '广东-广州', 'dev-web-c5', 'FAIL', '签名不一致');

INSERT INTO tm_risk_alert (trace_id, batch_id, risk_type, risk_level, description, status, detected_at, resolved_at) VALUES
('TRACE-APPLE-001', 1, 'FREQUENT_SCAN', 'HIGH', '同一溯源码在 5 分钟内出现高频异地扫描', 'OPEN', '2026-02-06 09:03:30', NULL),
('TRACE-STRAWBERRY-002', 18, 'SIGNATURE_MISMATCH', 'HIGH', '二维码签名校验失败，疑似篡改', 'OPEN', '2026-02-07 12:30:10', NULL);

INSERT INTO tm_audit_log (user_id, username, action, module, target_id, request_path, request_method, request_body, response_code, ip)
VALUES
(3, 'merchant01', 'CREATE_BATCH', 'BATCH', 'BATCH-APPLE-002', '/api/merchant/batches', 'POST', '{"batchNo":"BATCH-APPLE-002"}', '0', '10.0.0.3'),
(1, 'consumer01', 'PLACE_ORDER', 'ORDER', 'OD-202602-0001', '/api/orders', 'POST', '{"orderNo":"OD-202602-0001"}', '0', '10.0.0.11'),
(5, 'regulator01', 'VIEW_RISK_ALERT', 'RISK', 'TRACE-APPLE-001', '/api/regulator/alerts', 'GET', NULL, '0', '10.0.0.21');