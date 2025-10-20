# استخدم Maven مع Java 17 كـ base image
FROM maven:3.9.0-eclipse-temurin-17 AS build

# إنشاء مجلد العمل
WORKDIR /app

# نسخ كل محتوى Docker-1 داخل الحاوية
COPY . /app

# بناء التطبيق
RUN mvn clean package -DskipTests

# مرحلة التشغيل
FROM eclipse-temurin:17-jdk

WORKDIR /app

# نسخ jar من مرحلة build
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar /app/demo.jar

# تعيين المنفذ
EXPOSE 8080

# تشغيل التطبيق
ENTRYPOINT ["java","-jar","/app/demo.jar"]

