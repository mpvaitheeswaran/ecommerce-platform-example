# வலையங்காடி (Valai-Angadi) E-Commerce Microservices Platform (Dockerized)

A complete **Spring Boot Microservices-based E-Commerce backend system** built using:

- Java 25
- Spring Boot 4.0.2
- Spring Cloud (Eureka Service Registry)
- Docker & Docker Compose
- REST APIs
- Microservices Architecture

---

## Architecture Overview

This project follows a **Microservices Architecture** pattern.

### Services Included:

| Service | Port | Description |
|----------|------|-------------|
| Service Registry | 8762 | Eureka Server |
| Order Service | 8081 | Handles order processing |
| Inventory Service | 8082 | Manages product stock |
| Payment Service | 8083 | Handles payment processing |
| Notification Service | 8084 | Sends order notifications |
| User Service | 8085 | Manages users |
| Auth Service | 8086 | Authentication & Authorization |
| Product Service | 8087 | Product management |
| Cart Service | 8088 | Shopping cart management |

---

## 🐳 Dockerized Setup

All services are containerized using Docker.

### 🔹 Docker Compose Configuration

Each microservice runs on: Host Port -> Container Port (8080)
Example: 8082 -> 8080 (Inventory Service)

### 🔹 Run the Entire System
```bash
docker compose up --build
```
### 🔹 Stop Services
```bash
docker compose down
```
## 🌐 Service Registry
Eureka Dashboard: `http://127.0.0.1:8762`
## 🔥 Upcoming Enhancements
- API Gateway Integration

- Centralized Configuration Server

- Circuit Breaker (Resilience4j)

- Distributed Tracing

- Postgress and NoSQL Container Integration

## ⭐ Support
If you find this useful, please ⭐ the repository.