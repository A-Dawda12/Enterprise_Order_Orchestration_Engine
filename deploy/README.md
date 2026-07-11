# Local deployment

Infrastructure and runtime layout for the Enterprise Order Orchestration Engine.

**Plan:** [IMPLEMENTATION-PHASES.md](../docs/IMPLEMENTATION-PHASES.md) (Phase 1)

## Quick start

```bash
# From repo root
cd deploy
docker compose up -d postgres redis
```

## Port matrix

### Spring Boot services (Phase 0 scaffold — run via Maven locally)

| Service | Port | Context path | Health |
| ----------------------- | ---- | ---------------- | ------------------------------ |
| order-service | 8081 | `/orders` | `GET /orders/v1/health` |
| inventory-service | 8082 | `/inventory` | `GET /inventory/v1/health` |
| payment-service | 8083 | `/payments` | `GET /payments/v1/health` |
| fraud-service | 8084 | `/fraud` | `GET /fraud/v1/health` |
| shipping-service | 8085 | `/shipping` | `GET /shipping/v1/health` |
| notification-service | 8086 | `/notifications` | `GET /notifications/v1/health` |
| order-orchestration-app | 8090 | `/orchestration` | `GET /orchestration/v1/health` |

### Infrastructure (Phase 1 — Docker Compose)

| Component | Port | Status |
| -------------------- | ----- | ---------------------- |
| PostgreSQL | 5432 | **Active** (Phase 1.1) |
| Redis | 6379 | **Active** (Phase 1.2) |
| Kafka | 9092 | Planned (Phase 1.3) |
| Zeebe gateway (gRPC) | 26500 | Planned (Phase 1.4) |
| Operate UI | 8088 | Planned (Phase 1.4) |
| Tasklist UI | 8089 | Planned (Phase 1.4) |
| Prometheus | 9090 | Planned (Phase 1.5) |
| Grafana | 3000 | Planned (Phase 1.5) |

## PostgreSQL (Phase 1.1)

| Database | Used by |
| ----------------- | -------------------- |
| `order_db` | order-service |
| `inventory_db` | inventory-service |
| `payment_db` | payment-service |
| `fraud_db` | fraud-service |
| `shipping_db` | shipping-service |
| `notification_db` | notification-service |

**Credentials (local only):** user `postgres`, password `postgres`

**JDBC URL examples:**

```text
jdbc:postgresql://localhost:5432/order_db
jdbc:postgresql://localhost:5432/inventory_db
```

### Verify PostgreSQL

```bash
docker compose up -d postgres
docker compose ps
docker compose exec postgres psql -U postgres -c "\l"
```

Expected: six `*_db` databases listed.

---

## Redis (Phase 1.2)

Used for:

- Idempotency keys (Phase 2.2)
- Inventory locks (Phase 4.1)

**Connection (local):**

```text
Host: localhost
Port: 6379
Password: (none)
```

### Verify Redis

```bash
docker compose up -d redis
docker compose ps
docker compose exec redis redis-cli ping
```

Expected output:

```text
PONG
```

---

## BPMN

Process definitions live under:

```text
deploy/bpmn/
```

They are deployed to the Zeebe broker beginning in **Phase 6**.

---

## Observability

- Prometheus configuration: `deploy/prometheus/prometheus.yml`
- Grafana dashboards: `deploy/grafana/dashboards/` (Phase 10)