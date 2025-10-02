FROM openjdk:17-jdk-slim
LABEL authors="Alexis Ortiz"
ARG JAR_FILE=target/pagosonline-0.0.1.jar
COPY ${JAR_FILE} app_pagosonline.jar
EXPOSE 9094

ENTRYPOINT ["java", "-jar", "app_pagosonline.jar"]