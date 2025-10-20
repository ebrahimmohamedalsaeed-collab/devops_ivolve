# Task 8 Docker

# Task 8: Run Java Spring Boot App in a Docker Container

**Objective:** Build and run a Java Spring Boot application inside a Docker container.

---

## 1️⃣ Prerequisites

- Docker installed and running
- Maven installed
- Java 17 installed
- Git installed

## step1 Clone the Application

```
git clone https://github.com/Ibrahim-Adel15/Docker-1.git

 cd Docker-1
```

## step2 create dockerfile 

```
# Build Stage: Use Maven with Java 17
FROM maven:3.9.0-eclipse-temurin-17 AS build

WORKDIR /app

# Copy source code
COPY . /app

# Build the application
RUN mvn clean package -DskipTests

# Runtime Stage: Use Java 17
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar /app/demo.jar

# Expose application port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/demo.jar"]


```

## step3 Build Docker Image

```
docker build -t app1 .

>will appear

Successfully built 85af635531c0
Successfully tagged app1:latest

```

## step4 Run the Docker Container

```
docker run -d -p 8080:8080 --name app1_container app1

>on your vmware

curl http://localhost:8080

>will appear

Hello from Dockerized Spring Boot!

```

## step5 Stop and Remove Container

```
docker stop app1_container
docker rm app1_container
```
