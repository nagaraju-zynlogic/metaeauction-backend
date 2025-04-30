FROM openjdk:17
#WORKDIR /app
#COPY target/auction12-0.0.1-SNAPSHOT.jar /app/auction12-0.0.1-SNAPSHOT.jar
#EXPOSE 8080
#CMD ["java", "-jar", "auction12-0.0.1-SNAPSHOT.jar"]

# Use a valid Java 17 JRE base image from Eclipse Temurin
#FROM eclipse-temurin:17-jre

# Set the working directory inside the container
WORKDIR /app

# Copy the Spring Boot JAR into the container
COPY target/auction12-0.0.1-SNAPSHOT.jar /app/auction12-0.0.1-SNAPSHOT.jar

# Expose the application's port
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "auction12-0.0.1-SNAPSHOT.jar"]
