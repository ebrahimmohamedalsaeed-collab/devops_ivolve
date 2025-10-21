# task10: Multi-Stage Build for a Java Application

## 🎯 Objective
Learn how to create a **multi-stage Docker build** for a Java (Maven-based) application to reduce the final image size and improve build efficiency.

---

## Step1 Clone the Application Repository
```bash
git clone https://github.com/Ibrahim-Adel15/Docker-1.git
cd Docker-1
```

## Step2 Multi-Stage Dockerfile 

```

# ======== Stage 1: Build the Application ========
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ======== Stage 2: Run the Application ========
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Step3 Build the Docker Image
```
docker build -t app3 .
```

## Step4 Run the Container
```
docker run -d --name app3-container -p 8080:8080 app3
```

## Step5 Test the Application

```
http://localhost:8080  ## on your vm
http://192.168.202.128
>will appear
Hello from Dockerized Spring Boot!
```

## Step6 Stop and Remove the Container
```
docker stop app3-container
docker rm app3-container
```

      





