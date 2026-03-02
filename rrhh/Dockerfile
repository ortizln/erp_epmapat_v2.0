FROM eclipse-temurin:17-jdk-jammy
LABEL authors="Alexis Ortiz"
ARG JAR_FILE=target/rrhh-0.0.1.jar
COPY ${JAR_FILE} app_rrhh.jar
EXPOSE 9101
ENTRYPOINT ["java", "-jar", "app_rrhh.jar"]
