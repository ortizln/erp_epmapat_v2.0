FROM eclipse-temurin:17-jdk-jammy
LABEL authors="Alexis Ortiz"
ARG JAR_FILE=target/gateway-0.0.1.jar
COPY ${JAR_FILE} app_gateway.jar
EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app_gateway.jar"]