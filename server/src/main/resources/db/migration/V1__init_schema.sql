CREATE TABLE IF NOT EXISTS tm_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_code VARCHAR(32) NOT NULL UNIQUE,
    role_name VARCHAR(64) NOT NULL,
    description VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tm_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(64) NOT NULL,
    phone VARCHAR(32) NULL,
    email VARCHAR(128) NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    last_login_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tm_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_tm_user_role_user_role (user_id, role_id),
    CONSTRAINT fk_tm_user_role_user FOREIGN KEY (user_id) REFERENCES tm_user (id),
    CONSTRAINT fk_tm_user_role_role FOREIGN KEY (role_id) REFERENCES tm_role (id)
);

CREATE TABLE IF NOT EXISTS tm_shop (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_user_id BIGINT NULL,
    shop_name VARCHAR(128) NOT NULL,
    license_no VARCHAR(64) NOT NULL UNIQUE,
    contact_phone VARCHAR(32) NULL,
    address VARCHAR(255) NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    CONSTRAINT fk_tm_shop_owner FOREIGN KEY (owner_user_id) REFERENCES tm_user (id)
);

CREATE TABLE IF NOT EXISTS tm_fruit (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    shop_id BIGINT NOT NULL,
    fruit_name VARCHAR(128) NOT NULL,
    category VARCHAR(64) NOT NULL,
    origin VARCHAR(128) NULL,
    unit VARCHAR(16) NOT NULL DEFAULT 'kg',
    unit_price DECIMAL(10,2) NOT NULL,
    image_url VARCHAR(255) NULL,
    description VARCHAR(500) NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ON_SALE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    KEY idx_tm_fruit_shop_status (shop_id, status),
    CONSTRAINT fk_tm_fruit_shop FOREIGN KEY (shop_id) REFERENCES tm_shop (id)
);

CREATE TABLE IF NOT EXISTS tm_batch (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    fruit_id BIGINT NOT NULL,
    shop_id BIGINT NOT NULL,
    batch_no VARCHAR(64) NOT NULL,
    trace_id VARCHAR(64) NOT NULL,
    harvest_date DATE NOT NULL,
    expire_date DATE NULL,
    quantity DECIMAL(10,3) NOT NULL,
    remaining_quantity DECIMAL(10,3) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'IN_STOCK',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_tm_batch_batch_no (batch_no),
    UNIQUE KEY uk_tm_batch_trace_id (trace_id),
    KEY idx_tm_batch_fruit_status (fruit_id, status),
    KEY idx_tm_batch_shop_status (shop_id, status),
    CONSTRAINT fk_tm_batch_fruit FOREIGN KEY (fruit_id) REFERENCES tm_fruit (id),
    CONSTRAINT fk_tm_batch_shop FOREIGN KEY (shop_id) REFERENCES tm_shop (id)
);

CREATE TABLE IF NOT EXISTS tm_batch_event (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    batch_id BIGINT NOT NULL,
    event_type VARCHAR(64) NOT NULL,
    event_time DATETIME NOT NULL,
    location VARCHAR(128) NULL,
    operator_id BIGINT NULL,
    payload_json JSON NULL,
    event_hash VARCHAR(128) NOT NULL,
    anchor_id VARCHAR(128) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    KEY idx_tm_batch_event_batch_time (batch_id, event_time),
    KEY idx_tm_batch_event_hash (event_hash),
    CONSTRAINT fk_tm_batch_event_batch FOREIGN KEY (batch_id) REFERENCES tm_batch (id),
    CONSTRAINT fk_tm_batch_event_operator FOREIGN KEY (operator_id) REFERENCES tm_user (id)
);

CREATE TABLE IF NOT EXISTS tm_inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    batch_id BIGINT NOT NULL,
    stock_qty DECIMAL(10,3) NOT NULL,
    locked_qty DECIMAL(10,3) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_tm_inventory_batch (batch_id),
    CONSTRAINT fk_tm_inventory_batch FOREIGN KEY (batch_id) REFERENCES tm_batch (id)
);

CREATE TABLE IF NOT EXISTS tm_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    order_no VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'CREATED',
    total_amount DECIMAL(10,2) NOT NULL,
    payable_amount DECIMAL(10,2) NOT NULL,
    paid_at DATETIME NULL,
    payment_ref VARCHAR(128) NULL,
    shipping_address VARCHAR(255) NULL,
    remark VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_tm_order_order_no (order_no),
    KEY idx_tm_order_user_status_time (user_id, status, created_at),
    CONSTRAINT fk_tm_order_user FOREIGN KEY (user_id) REFERENCES tm_user (id)
);

CREATE TABLE IF NOT EXISTS tm_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    fruit_id BIGINT NOT NULL,
    batch_id BIGINT NOT NULL,
    quantity DECIMAL(10,3) NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    KEY idx_tm_order_item_order (order_id),
    CONSTRAINT fk_tm_order_item_order FOREIGN KEY (order_id) REFERENCES tm_order (id),
    CONSTRAINT fk_tm_order_item_fruit FOREIGN KEY (fruit_id) REFERENCES tm_fruit (id),
    CONSTRAINT fk_tm_order_item_batch FOREIGN KEY (batch_id) REFERENCES tm_batch (id)
);

CREATE TABLE IF NOT EXISTS tm_trace_code (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    batch_id BIGINT NOT NULL,
    trace_id VARCHAR(64) NOT NULL,
    qr_payload VARCHAR(512) NOT NULL,
    signature VARCHAR(128) NOT NULL,
    issued_at DATETIME NOT NULL,
    expires_at DATETIME NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_tm_trace_code_batch (batch_id),
    UNIQUE KEY uk_tm_trace_code_trace_id (trace_id),
    CONSTRAINT fk_tm_trace_code_batch FOREIGN KEY (batch_id) REFERENCES tm_batch (id)
);

CREATE TABLE IF NOT EXISTS tm_trace_scan_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    trace_id VARCHAR(64) NOT NULL,
    batch_id BIGINT NULL,
    scan_time DATETIME NOT NULL,
    ip VARCHAR(64) NOT NULL,
    geo VARCHAR(128) NULL,
    device_fingerprint VARCHAR(128) NULL,
    result_status VARCHAR(32) NOT NULL,
    result_reason VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    KEY idx_tm_trace_scan_trace_time (trace_id, scan_time),
    KEY idx_tm_trace_scan_device_time (device_fingerprint, scan_time),
    CONSTRAINT fk_tm_trace_scan_batch FOREIGN KEY (batch_id) REFERENCES tm_batch (id)
);

CREATE TABLE IF NOT EXISTS tm_risk_alert (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    trace_id VARCHAR(64) NOT NULL,
    batch_id BIGINT NULL,
    risk_type VARCHAR(64) NOT NULL,
    risk_level VARCHAR(32) NOT NULL,
    description VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
    detected_at DATETIME NOT NULL,
    resolved_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    KEY idx_tm_risk_alert_trace_status (trace_id, status),
    CONSTRAINT fk_tm_risk_alert_batch FOREIGN KEY (batch_id) REFERENCES tm_batch (id)
);

CREATE TABLE IF NOT EXISTS tm_chain_anchor (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    batch_event_id BIGINT NULL,
    event_hash VARCHAR(128) NOT NULL,
    anchor_id VARCHAR(128) NOT NULL,
    chain_type VARCHAR(32) NOT NULL,
    anchor_status VARCHAR(32) NOT NULL,
    anchored_at DATETIME NULL,
    verify_result VARCHAR(32) NULL,
    tx_hash VARCHAR(128) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_tm_chain_anchor_event_hash (event_hash),
    UNIQUE KEY uk_tm_chain_anchor_anchor_id (anchor_id),
    KEY idx_tm_chain_anchor_status_time (anchor_status, anchored_at),
    CONSTRAINT fk_tm_chain_anchor_event FOREIGN KEY (batch_event_id) REFERENCES tm_batch_event (id)
);

CREATE TABLE IF NOT EXISTS tm_audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NULL,
    username VARCHAR(64) NULL,
    action VARCHAR(128) NOT NULL,
    module VARCHAR(64) NOT NULL,
    target_id VARCHAR(64) NULL,
    request_path VARCHAR(255) NOT NULL,
    request_method VARCHAR(16) NOT NULL,
    request_body TEXT NULL,
    response_code VARCHAR(32) NULL,
    ip VARCHAR(64) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    KEY idx_tm_audit_log_user_time (user_id, created_at),
    KEY idx_tm_audit_log_module_time (module, created_at)
);