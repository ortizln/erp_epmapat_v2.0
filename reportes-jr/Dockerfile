FROM openjdk:17-jdk-slim
LABEL authors="Alexis Ortiz"
ARG JAR_FILE=target/reportes-jr-0.0.1.jar
COPY ${JAR_FILE} app_reportes-jr.jar
EXPOSE 9092

ENTRYPOINT ["java", "-jar", "app_reportes-jr.jar"]