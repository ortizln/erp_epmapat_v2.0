@echo off
setlocal enabledelayedexpansion

REM Lista de microservicios (carpetas)
set modules=config sri-files comercializacion eureka gestiondocumental login pagosonline recaudacion gateway reportes-jr

for %%m in (%modules%) do (
    echo ========== Compilando %%m ==========
    cd %%m
    call mvn clean package -DskipTests
    if errorlevel 1 (
        echo ❌ Error compilando %%m
        exit /b 1
    )
    cd ..
)

echo ✅ Todos los JARs se compilaron correctamente.
