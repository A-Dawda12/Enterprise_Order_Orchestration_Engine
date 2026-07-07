# Enterprise Order Orchestration Engine — High-Level Design (HLD)

| Field | Value |
|---|---|
| **Version** | 1.0.0 |
| **Status** | Draft |
| **Author** | Architecture Team |
| **Last Updated** | 2026-07-05 |

---

## 1. Executive Summary

The **Enterprise Order Orchestration Engine** is a distributed, event-driven order fulfillment platform that mirrors patterns used in telecom, banking, insurance, and logistics. Instead of a monolithic order processor, each business capability is implemented as an independent microservice. **Camunda 8 (Zeebe)** orchestrates the end-to-end workflow via BPMN, providing compensation (Saga), retries, human tasks, parallel execution, and timers.

This document describes the system at an architectural level: boundaries, data flows, non-functional requirements, and deployment topology. Detailed API contracts, BPMN element mappings, and schema definitions are in [LLD.md](./LLD.md).

---

## 2. Goals & Non-Goals

### 2.1 Goals

| # | Goal |
|---|---|
| G1 | Orchestrate multi-step order fulfillment with clear service boundaries |
| G2 | Support automatic compensation when downstream steps fail after prior steps succeed |
| G3 | Execute Inventory, Payment, and Fraud checks in parallel where safe |
| G4 | Pause workflow for human fraud review when score > 80 |
| G5 | Auto-cancel orders when payment is not completed within 15 minutes |
| G6 | Retry transient failures (e.g., shipping service down: 3 retries, 30s backoff) |
| G7 | Production-grade observability: structured logs, metrics, distributed tracing |
| G8 | Horizontally scalable stateless services; workflow state in Camunda + PostgreSQL |

### 2.2 Non-Goals (v1)

- Multi-region active-active deployment
- PCI-DSS certified payment vault (use tokenized mock/stub in personal project)
- Real carrier integrations (stub shipping provider)
- Customer-facing UI (API-only; fraud task via Camunda Tasklist)

---

## 3. Context Diagram

```mermaid
C4Context
    title System Context — Order Orchestration Engine

    Person(customer, "Customer / Client App", "Places orders via REST API")
    Person(manager, "Fraud Manager", "Reviews high-risk orders in Camunda Tasklist")

    System(order_engine, "Order Orchestration Engine", "Camunda-orchestrated microservices platform")

    System_Ext(payment_gateway, "Payment Gateway", "External PSP — Stripe/Adyen stub")
    System_Ext(carrier, "Carrier API", "Shipping label & tracking stub")
    System_Ext(email_sms, "Notification Provider", "Email/SMS stub")

    Rel(customer, order_engine, "POST /orders, GET /orders/{id}")
    Rel(manager, order_engine, "Complete fraud review tasks")
    Rel(order_engine, payment_gateway, "Authorize / Capture / Refund")
    Rel(order_engine, carrier, "Create shipment")
    Rel(order_engine, email_sms, "Send notifications")
```

---

## 4. Logical Architecture

```mermaid
flowchart TB
    subgraph clients [Clients]
        API[REST Clients]
        TL[Camunda Tasklist]
    end

    subgraph edge [Edge Layer]
        GW[API Gateway / Ingress<br/>Rate limit, TLS, routing]
    end

    subgraph orchestration [Orchestration Layer]
        WF[Workflow Service<br/>Zeebe Gateway + Job Workers]
        ZB[(Zeebe Broker Cluster)]
        OP[Operate + Tasklist]
    end

    subgraph domain [Domain Microservices]
        OS[Order Service]
        IS[Inventory Service]
        PS[Payment Service]
        FS[Fraud Service]
        SS[Shipping Service]
        NS[Notification Service]
    end

    subgraph messaging [Event Bus]
        KF{{Apache Kafka<br/>order.* topics}}
    end

    subgraph data [Data Stores]
        PG_O[(PostgreSQL — Order DB)]
        PG_I[(PostgreSQL — Inventory DB)]
        PG_P[(PostgreSQL — Payment DB)]
        PG_F[(PostgreSQL — Fraud DB)]
        PG_S[(PostgreSQL — Shipping DB)]
        RD[(Redis Cluster<br/>Idempotency, cache, locks)]
    end

    subgraph observability [Observability]
        PROM[Prometheus]
        GRAF[Grafana]
        LOKI[Loki / ELK]
    end

    API --> GW
    GW --> OS
    GW --> WF
    TL --> OP

    OS -->|start process| WF
    WF <--> ZB
    WF -->|job workers invoke| IS & PS & FS & SS & NS & OS

    OS --> KF
    IS --> KF
    PS --> KF
    FS --> KF
    SS --> KF
    NS --> KF

    OS --> PG_O
    IS --> PG_I
    PS --> PG_P
    FS --> PG_F
    SS --> PG_S

    OS & IS & PS & FS & SS & NS & WF --> RD

    domain --> PROM
    WF --> PROM
    PROM --> GRAF
    domain --> LOKI
```

---

## 5. Microservice Responsibilities

| Service | Port | Responsibility | Owns Data |
|---|---|---|---|
| **Order Service** | 8081 | Order CRUD, validation rules, idempotency, publishes `order.created` | `orders`, `order_items`, `order_events` |
| **Inventory Service** | 8082 | Stock reservation, release, availability queries | `inventory`, `reservations` |
| **Payment Service** | 8083 | Authorize, capture, refund; payment timeout tracking | `payments`, `refunds` |
| **Fraud Service** | 8084 | Risk scoring, rules engine; triggers human review | `fraud_assessments`, `review_decisions` |
| **Shipping Service** | 8085 | Shipment creation, label generation, cancellation | `shipments` |
| **Notification Service** | 8086 | Email/SMS/push templated notifications | `notification_log` (outbox) |
| **Workflow Service** | 8080 | Zeebe process deployment, job workers, compensation handlers | None (orchestrator only; Camunda owns workflow state) |

> **Note:** Invoice generation is handled by Order Service (PDF/metadata) triggered by Workflow Service job worker, keeping service count aligned with the problem statement while avoiding an extra deployable for v1.

---

## 6. Order Fulfillment Workflow (BPMN Overview)

```mermaid
flowchart TD
    A([Order Received]) --> B[Validate Order]
    B --> C{Valid?}
    C -->|No| X([Reject Order])
    C -->|Yes| D{{Parallel Gateway}}

    D --> E[Reserve Inventory]
    D --> F[Authorize Payment]
    D --> G[Fraud Check]

    E --> H{{Join Gateway}}
    F --> H
    G --> H

    H --> I{Fraud score > 80?}
    I -->|Yes| J[Human Task: Manager Review]
    J --> K{Approved?}
    K -->|No| COMP([Compensate & Cancel])
    K -->|Yes| L[Capture Payment]
    I -->|No| L

    L --> M[Create Shipment]
    M --> N[Send Notification]
    N --> O[Generate Invoice]
    O --> P([Complete])

    F -.->|15 min timer| T[Cancel Order Timer]
    T --> COMP

    M -.->|fail after retries| COMP
    E -.->|fail| COMP

    COMP --> R[Refund Payment]
    R --> S[Cancel Shipment]
    S --> T2[Notify User — Failure]
    T2 --> X2([Order Cancelled])
```

### 6.1 BPMN Advanced Features Mapping

| Feature | BPMN Element | Behavior |
|---|---|---|
| **Parallel Gateway** | `ParallelGateway` (fork/join) | Inventory, Payment auth, Fraud run concurrently; join waits for all |
| **Compensation** | `Event Subprocess` + `Compensation Tasks` | On failure after payment: refund → cancel shipment → notify |
| **Retry** | `Service Task` + `Retry Backoff` (job worker) | Shipping: 3 attempts, 30s exponential backoff |
| **Human Task** | `User Task` linked to Camunda Tasklist | Fraud score > 80 → assign to `fraud-managers` group |
| **Timer** | `Boundary Timer Event` on Payment | PT15M → escalate to cancel subprocess |

BPMN file: `workflow-service/src/main/resources/bpmn/order-fulfillment.bpmn` (see LLD).

---

## 7. Communication Patterns

### 7.1 Synchronous (Orchestration)

Camunda **job workers** in Workflow Service call domain services via **REST** (OpenAPI-generated clients). This keeps orchestration logic in BPMN and business logic in domain services.

```
Workflow Service (Job Worker) ──HTTP──▶ Inventory Service
                              ◀──JSON──
```

### 7.2 Asynchronous (Eventing)

Domain services publish domain events to Kafka for audit, analytics, and decoupled side effects.

| Topic | Producer | Consumers |
|---|---|---|
| `order.events` | Order Service | Notification (optional), audit pipeline |
| `inventory.events` | Inventory Service | Analytics |
| `payment.events` | Payment Service | Order Service (status sync), audit |
| `fraud.events` | Fraud Service | Audit |
| `shipping.events` | Shipping Service | Notification |
| `notification.events` | Notification Service | Audit |

### 7.3 Idempotency

All mutating APIs accept `Idempotency-Key` header. Keys are stored in **Redis** (TTL 24h) per service to guarantee exactly-once semantics at the application layer.

---

## 8. Data Architecture

### 8.1 Database-per-Service

Each microservice owns its PostgreSQL schema. **No cross-service DB joins.** Cross-service queries go through APIs or Kafka event projections.

### 8.2 Redis Usage

| Use Case | Key Pattern | TTL |
|---|---|---|
| Idempotency | `{service}:idempotency:{key}` | 24h |
| Inventory reservation lock | `inventory:lock:{sku}` | 5m |
| Fraud score cache | `fraud:score:{customerId}` | 1h |
| Rate limiting | `ratelimit:{clientId}:{endpoint}` | 1m |

### 8.3 Event Sourcing (Lightweight)

Order Service maintains an `order_events` append-only table for audit/replay. Not full event sourcing — operational state remains in `orders`.

---

## 9. Scalability & Resilience

### 9.1 Horizontal Scaling

| Component | Scaling Strategy |
|---|---|
| Domain microservices | Stateless pods; HPA on CPU + custom metric (request latency p95) |
| Workflow job workers | Scale on `zeebe_backlog` metric (pending jobs per job type) |
| Zeebe brokers | Partition-based; 3+ brokers for HA |
| Kafka | Partition by `orderId`; min 6 partitions for `order.events` |
| PostgreSQL | Primary + read replicas per service (v1: single instance acceptable locally) |
| Redis | Cluster mode (3 masters) in prod |

### 9.2 Failure Handling

| Failure | Response |
|---|---|
| Transient HTTP 5xx | Zeebe job retry with backoff |
| Permanent business failure (out of stock) | Trigger compensation subprocess |
| Broker partition loss | Zeebe replication factor 3 |
| Kafka consumer lag | Scale consumer group |
| Redis unavailable | Fail open for cache; fail closed for idempotency (503) |

### 9.3 Capacity Planning (Reference)

| Metric | Target (prod reference) |
|---|---|
| Orders/sec | 500 sustained, 2000 burst |
| p99 end-to-end latency | < 30s (excluding human task wait) |
| Availability | 99.9% (excluding planned maintenance) |

---

## 10. Security

| Layer | Control |
|---|---|
| Transport | TLS 1.3 everywhere (mTLS service-to-service in prod) |
| Authentication | OAuth2 JWT (Keycloak) for external clients; service accounts internally |
| Authorization | RBAC: `order:write`, `fraud:review`, etc. |
| Secrets | Vault / K8s Secrets; never in repo |
| PII | Mask in logs via logback patterns; encrypt at rest |
| API | Rate limiting at gateway (100 req/min/client default) |

---

## 11. Observability

### 11.1 Three Pillars

| Pillar | Tool | Details |
|---|---|---|
| **Logs** | Logback → Loki/ELK | Structured JSON; per-service `logback-spring.xml` (see LLD §12) |
| **Metrics** | Micrometer → Prometheus | RED metrics per endpoint; custom business counters |
| **Traces** | OpenTelemetry → Tempo/Jaeger | W3C `traceparent` propagated across services |

### 11.2 Key Dashboards (Grafana)

- Order throughput & success rate
- Workflow instance duration by stage
- Compensation trigger rate
- Fraud human task queue depth
- Kafka consumer lag
- Service SLA (p50/p95/p99 latency)

### 11.3 Alerting

| Alert | Condition |
|---|---|
| High order failure rate | `order_failure_rate > 5%` for 5m |
| Payment timeout spike | `payment_timeout_total` anomaly |
| Zeebe backlog | `pending_jobs > 1000` for 10m |
| Shipping retry exhaustion | `shipping_retry_exhausted_total > 0` |

---

## 12. Deployment Topology

### 12.1 Local (Docker Compose)

```
┌─────────────────────────────────────────────────────────┐
│  docker-compose.yml                                      │
│  ├── postgres (6 schemas via init scripts)              │
│  ├── redis                                               │
│  ├── kafka + zookeeper (or KRaft)                       │
│  ├── camunda (Zeebe + Operate + Tasklist + Elasticsearch)│
│  ├── prometheus + grafana                               │
│  └── 7 Spring Boot services                             │
└─────────────────────────────────────────────────────────┘
```

### 12.2 Production (Kubernetes)

```mermaid
flowchart LR
    subgraph k8s [Kubernetes Cluster]
        ING[Ingress NGINX]
        subgraph ns_app [namespace: order-engine]
            OS_P[order-service x3]
            IS_P[inventory-service x3]
            PS_P[payment-service x3]
            FS_P[fraud-service x2]
            SS_P[shipping-service x3]
            NS_P[notification-service x2]
            WF_P[workflow-service x3]
        end
        subgraph ns_camunda [namespace: camunda]
            ZB_P[Zeebe x3]
            OP_P[Operate]
            TL_P[Tasklist]
        end
        subgraph ns_data [namespace: data]
            PG_P[(CloudNativePG)]
            RD_P[(Redis Cluster)]
            KF_P[(Strimzi Kafka)]
        end
        subgraph ns_obs [namespace: observability]
            PR_P[Prometheus]
            GR_P[Grafana]
        end
    end
    ING --> ns_app
```

### 12.3 CI/CD Pipeline

```
Push → Build (Maven) → Unit Tests → Integration Tests (Testcontainers)
     → Container Scan (Trivy) → Push to Registry → Deploy to Staging
     → Contract Tests (Pact) → Deploy to Prod (ArgoCD, blue/green)
```

---

## 13. Technology Stack Summary

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 21 |
| Framework | Spring Boot | 3.3.x |
| Orchestration | Camunda 8 (Zeebe) | 8.5.x |
| Messaging | Apache Kafka | 3.7.x |
| RDBMS | PostgreSQL | 16 |
| Cache | Redis | 7.x |
| API Docs | SpringDoc OpenAPI | 2.x |
| Metrics | Micrometer + Prometheus | — |
| Dashboards | Grafana | 10.x |
| Containers | Docker | 24.x |
| Orchestration (prod) | Kubernetes | 1.29+ |

---

## 14. Service URL Map (Runtime)

Base URL pattern: `https://{env}.order-engine.example.com`

| Service | Base Path | Health | OpenAPI |
|---|---|---|---|
| Workflow Service | `/workflow` | `/workflow/actuator/health` | `/workflow/v3/api-docs` |
| Order Service | `/orders` | `/orders/actuator/health` | `/orders/v3/api-docs` |
| Inventory Service | `/inventory` | `/inventory/actuator/health` | `/inventory/v3/api-docs` |
| Payment Service | `/payments` | `/payments/actuator/health` | `/payments/v3/api-docs` |
| Fraud Service | `/fraud` | `/fraud/actuator/health` | `/fraud/v3/api-docs` |
| Shipping Service | `/shipping` | `/shipping/actuator/health` | `/shipping/v3/api-docs` |
| Notification Service | `/notifications` | `/notifications/actuator/health` | `/notifications/v3/api-docs` |

Local defaults: `http://localhost:{port}` — see [LLD.md §3](./LLD.md#3-microservice-api-catalog).

---

## 15. Risks & Mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| Camunda 8 operational complexity | High | Docker Compose profile for local; managed Camunda SaaS option for prod |
| Distributed transaction inconsistency | High | BPMN compensation + idempotent APIs |
| Kafka ordering across partitions | Medium | Partition key = `orderId` |
| Human task SLA breach | Medium | Escalation timer on user task (24h → auto-reject) |
| Log volume cost | Medium | Sampling for debug; INFO default in prod |

---

## 16. Document References

| Document | Path |
|---|---|
| Low-Level Design | [docs/LLD.md](./LLD.md) |
| BPMN Diagram | `workflow-service/src/main/resources/bpmn/order-fulfillment.bpmn` |
| OpenAPI Specs | `{service}/src/main/resources/openapi/` |
| Log Configs | `{service}/src/main/resources/logback-spring.xml` |
| Docker Compose | `deploy/docker-compose.yml` |

---

## 17. Glossary

| Term | Definition |
|---|---|
| **Saga** | Sequence of local transactions with compensating actions |
| **Job Worker** | Spring component polling Zeebe for service tasks |
| **Compensation** | Undo previously completed steps (refund, release inventory) |
| **Idempotency Key** | Client-supplied key ensuring duplicate requests produce same result |
