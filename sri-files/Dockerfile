FROM openjdk:17-jdk-slim
LABEL authors="Alexis Ortiz"
ARG JAR_FILE=target/sri-files-0.0.1.jar
COPY ${JAR_FILE} app_sri-files.jar
EXPOSE 9096

ENTRYPOINT ["java", "-jar", "app_sri-files.jar"]