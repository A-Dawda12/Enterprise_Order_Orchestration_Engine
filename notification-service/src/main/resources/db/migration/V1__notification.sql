-- Notification Service schema (LLD §5.6)

CREATE TABLE notifications (
       notification_id VARCHAR(36) PRIMARY KEY,
       order_id        VARCHAR(36),
       customer_id     VARCHAR(36) NOT NULL,
       channel         VARCHAR(16) NOT NULL,
       template        VARCHAR(64) NOT NULL,
       status          VARCHAR(16) NOT NULL,
       recipient       VARCHAR(256),
       variables       JSONB,
       queued_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
       sent_at         TIMESTAMPTZ
);

CREATE INDEX idx_notifications_order_id
    ON notifications (order_id);

CREATE INDEX idx_notifications_customer_id
    ON notifications (customer_id);

CREATE INDEX idx_notifications_status
    ON notifications (status);