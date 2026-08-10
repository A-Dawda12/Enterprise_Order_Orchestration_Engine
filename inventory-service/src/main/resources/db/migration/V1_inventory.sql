-- Inventory Service schema (LLD §5.2)

CREATE TABLE inventory (
   sku                 VARCHAR(64) PRIMARY KEY,
   available_quantity  INT NOT NULL DEFAULT 0 CHECK (available_quantity >= 0),
   reserved_quantity   INT NOT NULL DEFAULT 0 CHECK (reserved_quantity >= 0),
   updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE reservations (
  reservation_id  VARCHAR(36) PRIMARY KEY,
  order_id        VARCHAR(36) NOT NULL UNIQUE,
  status          VARCHAR(32) NOT NULL,
  expires_at      TIMESTAMPTZ NOT NULL,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_reservations_order_id ON reservations (order_id);

CREATE INDEX idx_reservations_status  ON reservations (status);

CREATE TABLE reservation_items (
   id              BIGSERIAL PRIMARY KEY,
   reservation_id  VARCHAR(36) NOT NULL REFERENCES reservations (reservation_id),
   sku             VARCHAR(64) NOT NULL,
   quantity        INT NOT NULL CHECK (quantity > 0)
);

CREATE INDEX idx_reservation_items_reservation_id
    ON reservation_items (reservation_id);

-- Phase 4.1 seed
INSERT INTO inventory (
    sku,
    available_quantity,
    reserved_quantity
)
VALUES (
           'SKU-123',
           150,
           0
       );