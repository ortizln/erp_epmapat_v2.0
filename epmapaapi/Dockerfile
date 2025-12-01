FROM eclipse-temurin:17-jdk-jammy
LABEL authors="Alexis Ortiz"
ARG JAR_FILE=target/epmapaapi-0.0.1.jar
COPY ${JAR_FILE} app_epmapaapi.jar
EXPOSE 9098

ENTRYPOINT ["java", "-jar", "app_epmapaapi.jar"]