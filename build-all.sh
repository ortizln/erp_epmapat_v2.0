#!/bin/bash

# Lista de microservicios (carpetas)

for module in "${modules[@]}"
do
  echo "========== Compilando $module =========="
  cd "$module" || exit
  mvn clean package -DskipTests
  cd ..
done

echo "✅ Todos los JARs se compilaron correctamente."
