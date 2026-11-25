@echo off
setlocal EnableDelayedExpansion

set "RAIZ=%~dp0.."
set "JUNIT=%RAIZ%\lib\test\junit-4.13.2.jar"
set "HAMCREST=%RAIZ%\lib\test\hamcrest-core-1.3.jar"
set "DRIVER=%RAIZ%\lib\mysql-connector-j-9.4.0.jar"
set "CLASES=%RAIZ%\build\classes"
set "TEST_CLASES=%RAIZ%\build\test\classes"

call "%~dp0compilar.bat"
if errorlevel 1 exit /b 1

if not exist "%TEST_CLASES%" mkdir "%TEST_CLASES%"
set "FUENTES="
for /r "%RAIZ%\test" %%f in (*.java) do set "FUENTES=!FUENTES! "%%f""

echo Compilando pruebas...
javac -encoding UTF-8 -cp "%CLASES%;%DRIVER%;%JUNIT%;%HAMCREST%" -d "%TEST_CLASES%" !FUENTES!
if errorlevel 1 exit /b 1

echo Ejecutando pruebas...
java -cp "%CLASES%;%TEST_CLASES%;%DRIVER%;%JUNIT%;%HAMCREST%" ^
    org.junit.runner.JUnitCore ^
    tests.PasswordHasherTest ^
    tests.ValidacionesTest ^
    tests.RegistroSolicitudTest
endlocal
