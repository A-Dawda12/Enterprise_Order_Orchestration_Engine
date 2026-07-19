# Enterprise Order Orchestration Engine

Enterprise Order Orchestration Engine is a multi-module Java (Spring Boot) project that implements a Camunda-orchestrated order fulfillment platform.

This repository contains several modules (services, clients and an orchestration app) and uses Maven for build and dependency management.

# Enterprise Order Orchestration Engine

Camunda 8 (Zeebe) orchestrated order fulfillment platform built with Java 21 and Spring Boot 4.1.

Six domain microservices expose REST APIs, while the `order-orchestration-app` hosts Zeebe job workers that orchestrate the complete order fulfillment saga:

**Validation → Parallel Inventory / Payment / Fraud → Payment Capture → Shipping → Notification → Invoice → Complete**

The platform also supports compensation workflows for failures such as payment timeout, fraud rejection, and shipping failures.

---

## Documentation

| Document | Purpose |
|----------|---------|
| [HLD](docs/HLD.md) | Architecture and project goals (G1–G8) |
| [LLD](docs/LLD.md) | REST APIs, database schemas, Kafka events, logging |
| [IMPLEMENTATION-PATTERNS](docs/IMPLEMENTATION-PATTERNS.md) | Activity catalog, worker patterns, BPMN conventions |
| [IMPLEMENTATION-PHASES](docs/IMPLEMENTATION-PHASES.md) | **Step-by-step implementation guide (Start here)** |
| [deploy/README.md](deploy/README.md) | Local Docker infrastructure and runtime configuration |

---

## Architecture

### Domain Microservices

| Service | Port | Responsibility |
|----------|------|----------------|
| order-service | 8081 | Order lifecycle and invoice generation |
| inventory-service | 8082 | Inventory reservation and release |
| payment-service | 8083 | Authorization, capture, refund, void |
| fraud-service | 8084 | Fraud assessment and review |
| shipping-service | 8085 | Shipment creation and tracking |
| notification-service | 8086 | Customer notifications |

### Orchestration

| Component | Port | Responsibility |
|----------|------|----------------|
| order-orchestration-app | 8090 | Zeebe workers and workflow orchestration |

---

## Prerequisites

- Java 21
- Maven 3.9+
- Docker Desktop
- Git

---

## Build

```bash
mvn clean install -DskipTests
```

---

## Run a Service

Example:

```bash
mvn spring-boot:run -pl order-service
```

Verify the service:

```bash
curl http://localhost:8081/orders/v1/health
```

---

## Local Infrastructure

Start PostgreSQL and Redis:

```bash
cd deploy
docker compose up -d postgres redis
```

Verify running containers:

```bash
docker compose ps
```

See **deploy/README.md** for:

- Port matrix
- PostgreSQL databases
- Redis configuration
- Verification commands
- Future infrastructure roadmap

---

## Current Project Status

| Phase | Status |
|--------|--------|
| Phase 0 — Project Scaffold | ✅ Complete |
| Phase 1 — Local Infrastructure | ✅ Complete (PostgreSQL + Redis completed) |
| Phase 2 — Shared Libraries | 🚧 In Progress |
| Phase 3 — Order Service | ⏳ Not Started |
| Phase 4 — Domain Microservices | ⏳ Not Started |
| Phase 5 — OpenAPI Channel Clients | ⏳ Not Started |
| Phase 6 — Zeebe Orchestration | ⏳ Not Started |
| Phase 7 — Happy Path BPMN | ⏳ Not Started |
| Phase 8 — Advanced BPMN | ⏳ Not Started |
| Phase 9 — Compensation | ⏳ Not Started |
| Phase 10 — Kafka & Observability | ⏳ Not Started |
| Phase 11 — Integration Tests & Demo | ⏳ Not Started |

---

## Development Workflow

Follow the implementation guide strictly:

1. Start with **IMPLEMENTATION-PHASES.md**
2. Complete one micro-step at a time.
3. Verify each step before proceeding.
4. Do not scaffold an entire phase in a single commit.
5. Commit after every verified step.

---

## Technology Stack

- Java 21
- Spring Boot 4.1
- Camunda 8 (Zeebe)
- PostgreSQL
- Redis
- Kafka (Phase 10)
- Docker Compose
- Maven
- Micrometer
- Prometheus
- Grafana

---

## License

This project is intended for learning, portfolio, and demonstration purposes.

