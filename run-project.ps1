<#
run-project.ps1 — helper to build and run the project on Windows PowerShell
Usage: Open PowerShell, cd to project root (where mvnw.cmd is) and run:
    .\run-project.ps1
Or if execution policy prevents running scripts, run:
    powershell -ExecutionPolicy Bypass -File .\run-project.ps1
#>

Write-Host "===============================" -ForegroundColor Cyan
Write-Host "Demo project runner (PowerShell)" -ForegroundColor Cyan
Write-Host "===============================`n" -ForegroundColor Cyan

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$mvnw = Join-Path $scriptDir 'mvnw.cmd'

if (-not (Test-Path $mvnw)) {
    Write-Host "ERROR: mvnw.cmd not found in folder: $scriptDir" -ForegroundColor Red
    Write-Host "Make sure you run this script from the project root where mvnw.cmd is located."
    exit 1
}

# Check java
try {
    $javaInfo = & java -version 2>&1
    if ($LASTEXITCODE -ne 0) { throw "no java" }
    Write-Host "Java found:`n" -ForegroundColor Green
    $javaInfo | ForEach-Object { Write-Host $_ }
} catch {
    Write-Host "Java not found in PATH." -ForegroundColor Yellow
    $jdkPath = Read-Host "Enter full path to JDK home (or press Enter to abort)"
    if ([string]::IsNullOrWhiteSpace($jdkPath)) {
        Write-Host "Aborting — Java not available." -ForegroundColor Red
        exit 1
    }
    if (-not (Test-Path (Join-Path $jdkPath 'bin\java.exe'))) {
        Write-Host "The path you entered does not look like a JDK root (no bin\java.exe). Aborting." -ForegroundColor Red
        exit 1
    }
    Write-Host "Setting JAVA_HOME for this session to: $jdkPath" -ForegroundColor Green
    $env:JAVA_HOME = $jdkPath
    $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
    Write-Host "Note: to persist JAVA_HOME permanently, use setx from cmd.exe or set environment variables in Windows Settings." -ForegroundColor Yellow
}

# Build
Write-Host "`nBuilding project (may download dependencies)..." -ForegroundColor Cyan
$build = Start-Process -FilePath $mvnw -ArgumentList '-DskipTests','package' -NoNewWindow -Wait -PassThru
if ($build.ExitCode -ne 0) {
    Write-Host "Build failed with exit code $($build.ExitCode)." -ForegroundColor Red
    Write-Host "You can collect detailed log with: .\mvnw.cmd -DskipTests package -X > mvn-build.log 2>&1" -ForegroundColor Yellow
    exit $build.ExitCode
}

Write-Host "Build succeeded." -ForegroundColor Green

# Run
Write-Host "Starting application..." -ForegroundColor Cyan
$run = Start-Process -FilePath $mvnw -ArgumentList 'spring-boot:run' -NoNewWindow -Wait -PassThru
if ($run.ExitCode -ne 0) {
    Write-Host "Application failed to start. Exit code: $($run.ExitCode)" -ForegroundColor Red
    exit $run.ExitCode
}

Write-Host "Application stopped." -ForegroundColor Cyan
Read-Host -Prompt "Press Enter to finish"

