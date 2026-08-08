
CREATE TABLE IF NOT EXISTS invoices (
    invoice_id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL REFERENCES orders (order_id),
    payment_id VARCHAR(36) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    pdf_url VARCHAR(512),
    issued_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_invoices_order_id ON invoices (order_id);