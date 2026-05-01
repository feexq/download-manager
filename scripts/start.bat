@echo off
cd /d "%~dp0.."

if not exist "target\DownloadManager-1.0-SNAPSHOT.jar" (
    echo Project not built. Running initial setup...
    call mvnw.cmd clean install
)

echo Starting Download Manager...
call mvnw.cmd javafx:run
pause