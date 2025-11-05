FROM eclipse-temurin:17-jdk-jammy
LABEL authors="Alexis Ortiz"
ARG JAR_FILE=target/config-0.0.1.jar
COPY ${JAR_FILE} app_config.jar
EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app_config.jar"]