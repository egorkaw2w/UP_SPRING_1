@echo off
rem run-project.cmd — helper to build and run the project on Windows (cmd.exe)
rem Usage: double-click or run from cmd.exe in project root

echo ===============================
echo Demo project runner (Windows cmd)
echo ===============================

:: Check that mvnw.cmd exists
if not exist "%~dp0mvnw.cmd" (
  echo ERROR: mvnw.cmd not found in this folder: %~dp0
  echo Make sure you run this script from the project root where mvnw.cmd is located.
  pause
  exit /b 1
)

:: Check java
java -version >nul 2>&1
if %errorlevel% neq 0 (
  echo Java not found in PATH.
  echo You need to install JDK 17+ and set JAVA_HOME.
  set /p JDK_PATH=Enter full path to JDK home (or press Enter to abort):
  if "%JDK_PATH%"=="" (
    echo Aborting — Java not available.
    pause
    exit /b 1
  )
  if exist "%JDK_PATH%\bin\java.exe" (
    echo Setting JAVA_HOME for this session to: %JDK_PATH%
    set JAVA_HOME=%JDK_PATH%
    set PATH=%JAVA_HOME%\bin;%PATH%
    echo Optionally, to persist JAVA_HOME run: setx JAVA_HOME "%JDK_PATH%"
  ) else (
    echo The path you entered does not look like a JDK root (no bin\java.exe). Aborting.
    pause
    exit /b 1
  )
) else (
  echo Java found.
  java -version
)

:: Build with mvnw
echo.
echo Building project (may download dependencies)...
%~dp0mvnw.cmd -DskipTests package
if %errorlevel% neq 0 (
  echo Build failed. Check the output above.
  echo You can collect detailed log: .\mvnw.cmd -DskipTests package -X > mvn-build.log 2>&1
  pause
  exit /b 1
)

echo Build succeeded.

echo Starting application...
%~dp0mvnw.cmd spring-boot:run
if %errorlevel% neq 0 (
  echo Application failed to start.
  pause
  exit /b 1
)

pause

