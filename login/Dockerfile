FROM openjdk:17-jdk-slim
LABEL authors="Alexis Ortiz"
ARG JAR_FILE=target/login-0.0.1.jar
COPY ${JAR_FILE} app_login.jar
EXPOSE 9091

ENTRYPOINT ["java", "-jar", "app_login.jar"]