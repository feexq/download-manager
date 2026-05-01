#!/bin/bash
cd "$(dirname "$0")/.."
if [ ! -f "target/DownloadManager-1.0-SNAPSHOT.jar" ]; then
    echo "Project not built. Running initial setup..."
    ./mvnw clean install
fi
echo "Starting Download Manager..."
./mvnw javafx:run
