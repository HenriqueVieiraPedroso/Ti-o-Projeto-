@echo off
echo Compilando...
if not exist bin mkdir bin
javac -cp "lib\mysql-connector-j-9.7.0\mysql-connector-j-9.7.0.jar" -d bin connection\*.java model\*.java dao\*.java view\*.java
if %errorlevel% neq 0 (
    echo ERRO na compilacao!
    pause
    exit /b 1
)
echo Compilado com sucesso! Iniciando...
java -cp "bin;lib\mysql-connector-j-9.7.0\mysql-connector-j-9.7.0.jar" view.TelaLogin
pause
