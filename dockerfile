# Etapa 1: Construcción con Maven dentro del contenedor
FROM maven:3.9-eclipse-temurin-23 AS builder

WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests

# Etapa 2: Imagen final para ejecutar el JAR
FROM eclipse-temurin:23-jre

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
