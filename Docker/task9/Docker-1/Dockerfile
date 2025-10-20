# Use Java 17 base image
FROM eclipse-temurin:17-jdk

# Create work directory inside container
WORKDIR /app

# Copy the built JAR file into container
COPY target/demo-0.0.1-SNAPSHOT.jar /app/demo.jar

# Expose port 8080
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/demo.jar"]

