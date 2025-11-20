ALTER TABLE purchase
    ADD COLUMN product_id BIGINT NULL,
    ADD COLUMN quantity INT NULL,
    ADD COLUMN unit_price DECIMAL(18, 2) NULL,
    ADD COLUMN purchased_at DATETIME(6) NULL;

UPDATE purchase
SET purchased_at = created_at
WHERE purchased_at IS NULL;

ALTER TABLE purchase
    MODIFY COLUMN product_id BIGINT NOT NULL,
    MODIFY COLUMN quantity INT NOT NULL,
    MODIFY COLUMN unit_price DECIMAL(18, 2) NOT NULL,
    MODIFY COLUMN total_price DECIMAL(18, 2) NOT NULL,
    MODIFY COLUMN purchased_at DATETIME(6) NOT NULL;

ALTER TABLE purchase
    ADD CONSTRAINT fk_purchase_product
        FOREIGN KEY (product_id) REFERENCES product (id);
