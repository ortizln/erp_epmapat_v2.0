#!/bin/bash

# Lista de microservicios (carpetas)
modules=("config" "sri-files" "comercializacion" "eureka" "gestiondocumental" "login" "pagosonline" "recaudacion" "gateway" "reportes-jr")

for module in "${modules[@]}"
do
  echo "========== Compilando $module =========="
  cd "$module" || exit
  mvn clean package -DskipTests
  cd ..
done

echo "✅ Todos los JARs se compilaron correctamente."
