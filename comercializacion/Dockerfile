FROM openjdk:17-jdk-slim
LABEL authors="Alexis Ortiz"
ARG JAR_FILE=target/comercializacion-0.0.1.jar
COPY ${JAR_FILE} app_comercializacion.jar
EXPOSE 9093

ENTRYPOINT ["java", "-jar", "app_comercializacion.jar"]