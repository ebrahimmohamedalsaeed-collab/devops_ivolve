# استخدم Java 17 كـ base image
FROM eclipse-temurin:17-jdk

# إنشاء مجلد العمل
WORKDIR /app

# نسخ JAR من مجلد Docker-1 داخل الحاوية
COPY Docker-1/target/demo-0.0.1-SNAPSHOT.jar /app/demo.jar

# تعيين المنفذ
EXPOSE 8080

# تشغيل التطبيق
ENTRYPOINT ["java","-jar","/app/demo.jar"]

