# Local deployment

Infrastructure and runtime layout for the Enterprise Order Orchestration Engine.

**Plan:** [IMPLEMENTATION-PHASES.md](../docs/IMPLEMENTATION-PHASES.md) (Phase 1)

## Quick start

```bash
# From repo root
cd deploy
docker compose up -d postgres
```

## Port matrix

### Spring Boot services (Run locally via Maven)

| Service | Port | Context Path | Health Endpoint |
|---------|------|--------------|----------------|
| order-service | 8081 | /orders | GET /orders/v1/health |
| inventory-service | 8082 | /inventory | GET /inventory/v1/health |
| payment-service | 8083 | /payments | GET /payments/v1/health |
| fraud-service | 8084 | /fraud | GET /fraud/v1/health |
| shipping-service | 8085 | /shipping | GET /shipping/v1/health |
| notification-service | 8086 | /notifications | GET /notifications/v1/health |
| order-orchestration-app | 8090 | /orchestration | GET /orchestration/v1/health |

### Infrastructure (Docker Compose)

| Component | Port | Status |
|-----------|------|--------|
| PostgreSQL | 5432 | Active (Phase 1.1) |
| Redis | 6379 | Planned (Phase 1.2) |
| Kafka | 9092 | Planned (Phase 1.3) |
| Zeebe Gateway (gRPC) | 26500 | Planned (Phase 1.4) |
| Operate UI | 8088 | Planned (Phase 1.4) |
| Tasklist UI | 8089 | Planned (Phase 1.4) |
| Prometheus | 9090 | Planned (Phase 1.5) |
| Grafana | 3000 | Planned (Phase 1.5) |

## PostgreSQL (Phase 1.1)

| Database | Used by |
|----------|---------|
| order_db | order-service |
| inventory_db | inventory-service |
| payment_db | payment-service |
| fraud_db | fraud-service |
| shipping_db | shipping-service |
| notification_db | notification-service |

Credentials: user `postgres`, password `postgres`

### JDBC URLs

```text
jdbc:postgresql://localhost:5432/order_db
jdbc:postgresql://localhost:5432/inventory_db
jdbc:postgresql://localhost:5432/payment_db
jdbc:postgresql://localhost:5432/fraud_db
jdbc:postgresql://localhost:5432/shipping_db
jdbc:postgresql://localhost:5432/notification_db
```

## Verify PostgreSQL

```bash
docker compose up -d postgres
docker compose ps
docker compose exec postgres psql -U postgres -c "\l"
```

Expected: six *_db databases listed.

## BPMN

Process definitions live under `deploy/bpmn/` and are deployed to Zeebe in Phase 6+.

## Observability

- Prometheus config: `deploy/prometheus/prometheus.yml`
- Grafana dashboards: `deploy/grafana/dashboards/` (Phase 10)
