# Docker folder
# Task 9: Run Java Spring Boot App in a Docker Container (JAR Only)

**Objective:** Build the Spring Boot application JAR first, then run it inside a Docker container using Java 17.

---

## 1️⃣ Prerequisites

- Docker installed and running
- Maven installed
- Java 17 installed
- Git installed

---

## step1 Clone the Application

```
git clone https://github.com/Ibrahim-Adel15/Docker-1.git
cd Docker-1
```

## step2 Create Dockerfile

```
# Use Java 17 base image
FROM eclipse-temurin:17-jdk

# Create work directory inside container
WORKDIR /app

# Copy the built JAR file into container
COPY target/demo-0.0.1-SNAPSHOT.jar /app/demo.jar

# Expose application port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/demo.jar"]
```

## step3 Build the Application JAR

```
 mvn clean package -DskipTests
```

## step4 Build Docker Image

```
docker build -t app2 .
```

## step5 Run the Docker Container

```
docker run -d -p 8080:8080 --name app2_container app2

>on your vm
http://localhost:8080
>will appear
Hello from Dockerized Spring Boot!

```

## Stop and Remove Container

```
docker stop app2_container
docker rm app2_container
```
