FROM openjdk:23-jdk-slim
LABEL authors="Saya"

WORKDIR /app

COPY build/libs/EndTermProject.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]
