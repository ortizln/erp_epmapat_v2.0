#!/bin/bash

# Lista de microservicios (carpetas)
modules=("config" "eureka" "gateway" "login" "recaudacion" "sri-files" "comercializacion")

for module in "${modules[@]}"
do
  echo "========== Compilando $module =========="
  cd "$module" || exit
  mvn clean package -DskipTests
  cd ..
done

echo "✅ Todos los JARs se compilaron correctamente."
