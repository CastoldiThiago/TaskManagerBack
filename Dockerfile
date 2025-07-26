# Etapa 1: build
FROM maven:3.9.6-amazoncorretto-17 as builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: run
FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
COPY --from=builder /app/target/TaskManager-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]