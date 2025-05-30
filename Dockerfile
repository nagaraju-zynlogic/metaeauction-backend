# ------------ Stage 1: Build the application ------------
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app

# Cache dependencies (this will speed up future builds)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source files
COPY src ./src

# Build the app (skip tests for speed)
RUN mvn clean package -DskipTests

# ------------ Stage 2: Minimal runtime image ------------
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

# Set timezone
ENV TZ=Asia/Kolkata
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone && \
    apk del tzdata

# Copy the JAR file from the builder stage
COPY --from=builder /app/target/auction12-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the app with performance-focused JVM flags
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:+TieredCompilation", "-XX:TieredStopAtLevel=1", "-jar", "app.jar"]
