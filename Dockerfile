FROM openjdk:17-jre-slim
WORKDIR /app
COPY target/auction12-0.0.1-SNAPSHOT.jar /app/auction12-0.0.1-SNAPSHOT.jar
EXPOSE 8080
CMD ["java", "-jar", "auction12-0.0.1-SNAPSHOT.jar"]