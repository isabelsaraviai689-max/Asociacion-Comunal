#!/bin/bash
# Compila, empaqueta y ejecuta el sistema. Requiere JDK 17+, Maven y MySQL con database/schema.sql cargado.
set -e
cd "$(dirname "$0")"
command -v mvn >/dev/null 2>&1 || { echo "Maven no esta instalado (o abra el proyecto en IntelliJ y ejecute Main)."; exit 1; }
echo "[1/2] Compilando y empaquetando..."
mvn -q clean package -DskipTests
echo "[2/2] Iniciando..."
java -jar target/asociacion-comunal.jar
