FROM openjdk:17-jdk-slim
LABEL authors="Alexis Ortiz"
ARG JAR_FILE=target/eureka-0.0.1.jar
COPY ${JAR_FILE} app_eureka.jar
EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app_eureka.jar"]