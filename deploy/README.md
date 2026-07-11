Create the complete local deployment setup for the Enterprise Order Orchestration Engine.

Requirements:

## Overview
The project consists of multiple Spring Boot microservices that run locally using Maven, while infrastructure components run using Docker Compose.

### Spring Boot Services
| Service | Port | Context Path | Health Endpoint |
|---------|------|--------------|----------------|
| order-service | 8081 | /orders | GET /orders/v1/health |
| inventory-service | 8082 | /inventory | GET /inventory/v1/health |
| payment-service | 8083 | /payments | GET /payments/v1/health |
| fraud-service | 8084 | /fraud | GET /fraud/v1/health |
| shipping-service | 8085 | /shipping | GET /shipping/v1/health |
| notification-service | 8086 | /notifications | GET /notifications/v1/health |
| order-orchestration-app | 8090 | /orchestration | GET /orchestration/v1/health |

These services should NOT be containerized initially and will be started via Maven.

## Infrastructure (Docker Compose)

Phase 1.1
- PostgreSQL
- Port: 5432
- Username: postgres
- Password: postgres

Create the following databases automatically on startup:

- order_db
- inventory_db
- payment_db
- fraud_db
- shipping_db
- notification_db

JDBC URLs:

jdbc:postgresql://localhost:5432/order_db
jdbc:postgresql://localhost:5432/inventory_db
jdbc:postgresql://localhost:5432/payment_db
jdbc:postgresql://localhost:5432/fraud_db
jdbc:postgresql://localhost:5432/shipping_db
jdbc:postgresql://localhost:5432/notification_db

Future infrastructure (prepare placeholders in docker-compose):

- Redis (6379)
- Kafka (9092)
- Zeebe Gateway (26500)
- Operate UI (8088)
- Tasklist UI (8089)
- Prometheus (9090)
- Grafana (3000)

These can remain commented or disabled for now.

## Folder Structure

deploy/
├── docker-compose.yml
├── postgres/
│   ├── init.sql
│   └── README.md
├── bpmn/
├── prometheus/
│   └── prometheus.yml
├── grafana/
│   └── dashboards/
└── README.md

## PostgreSQL Initialization

Use an initialization SQL script that creates all six databases automatically when the PostgreSQL container starts.

## README

Generate a README containing:

Quick Start

```bash
cd deploy
docker compose up -d postgres