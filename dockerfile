# Etapa 1: Construcción de la aplicación con Maven
FROM maven:3.9-eclipse-temurin-23 AS builder

# Establece el directorio de trabajo
WORKDIR /app

# Copia el código fuente del proyecto
COPY . .

# Ejecuta el build con Maven (sin pruebas)
RUN mvn clean package -DskipTests

# Etapa 2: Imagen liviana para ejecutar el JAR
FROM eclipse-temurin:23-jre

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo JAR generado desde el builder
COPY --from=builder /app/target/*.jar app.jar

# Expone el puerto que usa tu app Spring Boot
EXPOSE 8080

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
