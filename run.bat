@echo off
setlocal
if exist "%~dp0local-env.bat" call "%~dp0local-env.bat"
set "BUILD_DIR=%TEMP%\from-bilkenter-target"
mvn -q -Dproject.build.directory="%BUILD_DIR%" javafx:run
