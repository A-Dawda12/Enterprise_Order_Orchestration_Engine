
CREATE TABLE shipments (
    shipment_id        VARCHAR(36) PRIMARY KEY,
    order_id           VARCHAR(36) NOT NULL,
    reservation_id     VARCHAR(36),
    status             VARCHAR(32) NOT NULL,
    carrier            VARCHAR(32),
    tracking_number    VARCHAR(64),
    label_url          VARCHAR(512),
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    cancelled_at       TIMESTAMPTZ
);

CREATE INDEX idx_shipments_order_id ON shipments (order_id);
CREATE INDEX idx_shipments_status ON shipments (status);