FROM eclipse-temurin:17-jdk-jammy
LABEL authors="Alexis Ortiz"
ARG JAR_FILE=target/contabilidad-0.0.1.jar
COPY ${JAR_FILE} app_contabilidad.jar
EXPOSE 9102
ENTRYPOINT ["java", "-jar", "app_contabilidad.jar"]
