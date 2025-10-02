FROM openjdk:17-jdk-slim
LABEL authors="Alexis Ortiz"
# Instalar librerías necesarias para JasperReports
RUN apt-get update && apt-get install -y \
    libfreetype6 \
    libfontconfig1 \
    libxrender1 \
    libxext6 \
    && rm -rf /var/lib/apt/lists/*
ARG JAR_FILE=target/reportes-jr-0.0.1.jar
COPY ${JAR_FILE} app_reportes-jr.jar
EXPOSE 9097

ENTRYPOINT ["java", "-jar", "app_reportes-jr.jar"]