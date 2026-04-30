@echo off
cd ..
if not exist "target\DownloadManager-1.0-SNAPSHOT.jar" (
    echo Project not built. Running initial setup...
    call mvn clean install
)
echo Starting Download Manager...
mvn javafx:run
pause
