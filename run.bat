@echo off
title Asociacion Comunal - Grupo 11
cd /d "%~dp0"
where mvn >nul 2>nul || ( echo Maven no esta instalado. Abra el proyecto en IntelliJ y ejecute Main. & pause & exit /b 1 )
echo [1/2] Compilando y empaquetando...
call mvn -q clean package -DskipTests
if errorlevel 1 ( echo Error al compilar. & pause & exit /b 1 )
echo [2/2] Iniciando...
java -jar target\asociacion-comunal.jar
pause
