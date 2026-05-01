#!/bin/bash
cd "$(dirname "$0")/.."
echo "Starting P2P Central Server..."
java -cp target/DownloadManager-1.0-SNAPSHOT.jar com.project.downloadmanager.ServerStart
