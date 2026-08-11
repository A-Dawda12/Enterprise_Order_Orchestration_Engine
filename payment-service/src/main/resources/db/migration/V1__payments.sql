-- Payment Service schema (LLD §5.3)

CREATE TABLE payments (
      payment_id        VARCHAR(36) PRIMARY KEY,
      order_id          VARCHAR(36) NOT NULL,
      status            VARCHAR(32) NOT NULL,
      authorized_amount DECIMAL(12,2),
      captured_amount   DECIMAL(12,2) DEFAULT 0,
      currency          CHAR(3) NOT NULL,
      gateway_ref       VARCHAR(128),
      authorized_at     TIMESTAMPTZ,
      captured_at       TIMESTAMPTZ,
      created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_payments_order_id ON payments (order_id);
CREATE INDEX idx_payments_status ON payments (status);

CREATE TABLE refunds (
     refund_id    VARCHAR(36) PRIMARY KEY,
     payment_id   VARCHAR(36) NOT NULL REFERENCES payments (payment_id),
     amount       DECIMAL(12,2) NOT NULL,
     reason       VARCHAR(128),
     status       VARCHAR(32) NOT NULL,
     refunded_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refunds_payment_id ON refunds (payment_id);