@echo off
title Asociacion Comunal - Grupo 10
if not exist out mkdir out
dir /s /b src\*.java > fuentes.txt
javac -d out @fuentes.txt
if errorlevel 1 ( echo Error al compilar. & pause & exit /b 1 )
del fuentes.txt
java -cp out com.sv.grupo10.asociacioncomunal.main.Main
pause
