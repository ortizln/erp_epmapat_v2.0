FROM eclipse-temurin:17-jdk-jammy
LABEL authors="Alexis Ortiz"
ARG JAR_FILE=target/gestiondocumental-0.0.1.jar
COPY ${JAR_FILE} app_gestiondocumental.jar
EXPOSE 9094

ENTRYPOINT ["java", "-jar", "app_gestiondocumental.jar"]