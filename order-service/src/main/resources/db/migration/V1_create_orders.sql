-- Order Service Schema (LLD §5.1)

CREATE TABLE orders (
    order_id           VARCHAR(36) PRIMARY KEY,
    customer_id        VARCHAR(36) NOT NULL,
    status             VARCHAR(32) NOT NULL,
    total_amount       DECIMAL(12,2) NOT NULL,
    currency           CHAR(3) NOT NULL DEFAULT 'INR',
    workflow_key       VARCHAR(64),
    shipping_address   JSONB NOT NULL,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_orders_customer
    ON orders (customer_id);

CREATE INDEX idx_orders_status
    ON orders (status);

CREATE TABLE order_items (
     id                 BIGSERIAL PRIMARY KEY,
     order_id           VARCHAR(36) NOT NULL REFERENCES orders(order_id),
     sku                VARCHAR(64) NOT NULL,
     quantity           INT NOT NULL CHECK (quantity > 0),
     unit_price         DECIMAL(12,2) NOT NULL
);

CREATE INDEX idx_order_items_order_id
    ON order_items (order_id);

CREATE TABLE order_events (
      id                 BIGSERIAL PRIMARY KEY,
      order_id           VARCHAR(36) NOT NULL,
      event_type         VARCHAR(64) NOT NULL,
      payload            JSONB,
      created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_order_events_order_id
    ON order_events (order_id);

CREATE TABLE invoices (
      invoice_id         VARCHAR(36) PRIMARY KEY,
      order_id           VARCHAR(36) NOT NULL REFERENCES orders(order_id),
      payment_id         VARCHAR(36) NOT NULL,
      amount             DECIMAL(12,2) NOT NULL,
      pdf_url            VARCHAR(512),
      issued_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_invoices_order_id
    ON invoices (order_id);