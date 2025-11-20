-- refund Table
CREATE TABLE refund
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_id  BIGINT       NOT NULL COMMENT '환불 대상 구매 ID',
    reason       VARCHAR(255) NOT NULL COMMENT '환불 사유',
    status       VARCHAR(20)  NOT NULL DEFAULT 'REQUESTED' COMMENT 'REQUESTED, APPROVED, REJECTED',
    created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_refund_purchase
        FOREIGN KEY (purchase_id) REFERENCES purchase (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_refund_purchase_id ON refund (purchase_id);
