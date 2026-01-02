CREATE TABLE sales
(
    id             INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    product_id     BIGINT         NOT NULL,
    variant_id     BIGINT         NOT NULL,
    quantity       INT            NOT NULL DEFAULT 1,
    unit_price     DECIMAL(19, 2) NOT NULL,
    tax_rate       DECIMAL(5, 2)  NOT NULL DEFAULT 0.15,
    tax_amount     DECIMAL(19, 2) NOT NULL,
    total_amount   DECIMAL(19, 2) NOT NULL,
    payment_method VARCHAR(50)             DEFAULT 'CARD',
    sold_at        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_sales_sold_at ON sales (sold_at);
