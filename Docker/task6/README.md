# Task 6 Docker

# Lab 6: Building and Packaging Java Applications with Gradle

## Objective
Build and package a simple Java application using **Gradle**, run unit tests, and verify the application works correctly.

---

## Prerequisites
- Java 21 or later
- Gradle 8.10 or later
- Git

---

## Steps

### 1. Install Gradle
Ensure Gradle is installed and check the version:
```
sudo apt install gradle -y
```
---


###  instasll Ensure openjdk-21-jdk 
```
sudo apt install openjdk-21-jdk -y
```
---

check java and javac version
```
java -version

java -version
```

---

### step3 Clone Source Code

```
git clone https://github.com/Ibrahim-Adel15/build1.git
cd build1
```

---


### step4 Run Unit Tests

```
gradle clean test --no-daemon

>will apper

BUILD SUCCESSFUL in 13s
4 actionable tasks: 3 executed, 1 up-to-date
```
---


---

### step5 Build the Application

```
gradle build --no-daemon

>will apper

BUILD SUCCESSFUL in 11s
7 actionable tasks: 4 executed, 3 up-to-date
```

---

### step6 show file
```
ls -lh build/libs/

>will apper

-rw-rw-r-- 1 ebrahim ebrahim 936 Oct 20 11:38 ivolve-app.jar
```

### step7 Run the Application

```
java -jar build/libs/ivolve-app.jar

>after run

Hello iVolve Trainee
```












