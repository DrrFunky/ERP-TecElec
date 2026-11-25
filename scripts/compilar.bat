@echo off
setlocal EnableDelayedExpansion

set "RAIZ=%~dp0.."
set "DRIVER=%RAIZ%\lib\mysql-connector-j-9.4.0.jar"
set "SALIDA=%RAIZ%\build\classes"

if not exist "%SALIDA%" mkdir "%SALIDA%"

set "FUENTES="
for /r "%RAIZ%\src" %%f in (*.java) do set "FUENTES=!FUENTES! "%%f""

echo Compilando fuentes...
javac -encoding UTF-8 -cp "%DRIVER%" -d "%SALIDA%" !FUENTES!
if errorlevel 1 exit /b 1

echo Compilacion correcta: %SALIDA%
endlocal
