# Task 7 Docker

# Lab 7: Building and Packaging Java Applications with Maven

## Objective
Build and package a simple Java application using **Maven**, run unit tests, and verify the application works correctly.

---

## Prerequisites
- Java 21 or later
- Maven 3.8+
- Git

---

## Step1 install maven

```
sudo apt install maven -y

>check version
mvn -version
Apache Maven 3.8.7

```

## step2 Clone Source Code

```
git clone https://github.com/Ibrahim-Adel15/build2.git
cd build2

```
## step3 Run Unit Tests

```
 mvn test

```

## step4 Build the Application

```
mvn clean package

>show file
 ls -lh target/hello-ivolve-1.0-SNAPSHOT.jar

```

## step5 Run the Application

```
java -jar target/hello-ivolve-1.0-SNAPSHOT.jar

>will appear

Hello Ivolve Trainee


```








