FROM eclipse-temurin:17-jdk-jammy
LABEL authors="Alexis Ortiz"
ARG JAR_FILE=target/eureka-0.0.1.jar
COPY ${JAR_FILE} app_eureka.jar
EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app_eureka.jar"]