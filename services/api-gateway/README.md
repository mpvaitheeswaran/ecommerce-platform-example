# API Gateway

The API Gateway is the single entry point to the E-Commerce Microservices system.

It is built using:

- Spring Boot
- Spring Cloud Gateway
- Spring Cloud LoadBalancer
- Netflix Eureka (Service Discovery)
- Docker

---

## 📌 Overview

The API Gateway performs:

- Dynamic routing to microservices
- Service discovery via Eureka
- Load balancing using `lb://`
- Centralized entry point for all client requests
- Request filtering and forwarding

---

##  Architecture Role

Client → API Gateway → Microservices

The gateway does NOT hardcode service URLs.

Instead, it uses: lb://SERVICE-NAME  
Which resolves via Eureka.
---

## 🔧 Technologies Used

- Spring Boot
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka Client
- Spring Cloud LoadBalancer
- Docker

---

## 📂 Project Structure
```declarative
api-gateway
├── src/main/java
├── src/main/resources
│ ├── application.yml
│ └── application-docker.yml
├── Dockerfile
└── pom.xml
```
---

## ⚙️ Configuration

### application.yml

```yaml
spring:
  application:
    name: api-gateway

  cloud:
    gateway:
      routes:
        - id: order-service
          uri: lb://ORDER-SERVICE
          predicates:
            - Path=/orders/**

        - id: product-service
          uri: lb://PRODUCT-SERVICE
          predicates:
            - Path=/products/**

eureka:
  client:
    serviceUrl:
      defaultZone: http://service-registry:8762/eureka/
```
### Docker Configuration
The gateway runs inside Docker and communicates with services via Docker network DNS.

Example docker-compose snippet:
```yaml
api-gateway:
  build:
    context: .
    dockerfile: api-gateway/Dockerfile
  ports:
    - "8080:8080"
  depends_on:
    - service-registry
  networks:
    - app-network
```
---
## 🚀 Running the API Gateway
1. Ensure Eureka Server is running and services are registered.
2. Build and run the API Gateway using Docker Compose:
```bash
docker-compose up --build api-gateway
```
3. Access the gateway at `http://localhost:8080`.
4. Test routing:
   - `http://localhost:8080/orders` → routes to Order Service
   - `http://localhost:8080/products` → routes to Product Service
   - Eureka handles service discovery and load balancing automatically.
---
## 📚 Further Reading
- [Spring Cloud Gateway Documentation](https://spring.io/projects/spring-cloud-gateway)
- [Spring Cloud Netflix Eureka Documentation](https://spring.io/projects/spring-cloud-netflix)
- [Dockerizing Spring Boot Applications](https://spring.io/guides/gs/spring-boot-docker/)




