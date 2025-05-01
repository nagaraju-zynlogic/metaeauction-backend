# ------------ Stage 1: Build the application ------------
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source files and build the app
COPY src ./src
RUN mvn clean package -DskipTests

# ------------ Stage 2: Run the application ------------
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=builder /app/target/auction12-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Spring Boot default)
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
