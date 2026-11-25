@echo off
setlocal

set "RAIZ=%~dp0.."
set "DRIVER=%RAIZ%\lib\mysql-connector-j-9.4.0.jar"
set "CLASES=%RAIZ%\build\classes"

call "%~dp0compilar.bat"
if errorlevel 1 exit /b 1

rem Opciones JVM (por ejemplo -Ddb.url=... o -Ddb.usuario=...) se pasan como argumentos.
java %* -cp "%CLASES%;%DRIVER%" l2madevs.L2MADevs
endlocal
