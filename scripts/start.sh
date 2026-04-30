#!/bin/bash
cd ..
if [ ! -f "target/DownloadManager-1.0-SNAPSHOT.jar" ]; then
    echo "Project not built. Running initial setup..."
    mvn clean install
fi
echo "Starting Download Manager..."
mvn javafx:run
