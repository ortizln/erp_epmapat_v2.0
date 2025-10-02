FROM openjdk:17-jdk-slim
LABEL authors="Alexis Ortiz"
ARG JAR_FILE=target/recaudacion-0.0.1.jar
COPY ${JAR_FILE} app_recaudacion.jar
EXPOSE 9092

ENTRYPOINT ["java", "-jar", "app_recaudacion.jar"]