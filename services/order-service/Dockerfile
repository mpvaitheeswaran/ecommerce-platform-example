# -------- Stage 1: Build --------
FROM maven:amazoncorretto AS build

WORKDIR /app

# 1. Copy the Root Parent POM (from the root context)
COPY pom.xml .

# 2. Copy ALL module POMs (the skeleton)
# This satisfies Maven's requirement that all <modules> exist
COPY service-registry/pom.xml service-registry/
COPY order-service/pom.xml order-service/
COPY inventory-service/pom.xml inventory-service/
COPY payment-service/pom.xml payment-service/
COPY notification-service/pom.xml notification-service/
COPY user-service/pom.xml user-service/
COPY auth-service/pom.xml auth-service/

# 3. Cache dependencies for this specific module
RUN mvn -pl order-service dependency:go-offline -B

# 4. Copy the service-registry source code
COPY order-service/src order-service/src


# 5. Build only the order-service module
# -am: Also make dependencies (if order-service depends on a shared-module)
RUN mvn clean package -pl order-service -am -DskipTests

# -------- Stage 2: Run --------
FROM eclipse-temurin:25-jdk

WORKDIR /app

COPY --from=build /app/order-service/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]