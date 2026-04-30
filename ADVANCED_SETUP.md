# Advanced Setup: Browser Extensions & P2P

This guide explains how to configure the companion browser extensions and set up the Peer-to-Peer (P2P) file-sharing network.

---

## 🧩 Browser Extensions

The application includes a built-in HTTP server (port 8080) to receive download links directly from your browser.

### Installation
1. Open your browser's extension management page:
   - **Chrome/Edge**: `chrome://extensions/`
   - **Firefox**: `about:debugging#/runtime/this-firefox`
2. Enable **Developer Mode**.
3. Load the unpacked extension:
   - **Chrome/Edge**: Click **Load unpacked** and select the `src/main/resources/extensionForChrome` directory.
   - **Firefox**: Click **Load Temporary Add-on** and select `manifest.json` inside the `src/main/resources/extensionForMozilla` directory.

### Usage
Once installed, clicking supported file links in your browser will automatically send the URL to the DownloadManager application.

---

## 🌐 Peer-to-Peer (P2P) File Sharing

To share files directly with other users, you must connect to a central discovery server.

### 1. Start the Discovery Server
Run the following script to start the central P2P server:
```bash
./scripts/run-server.bat # Windows or ./scripts/run-server.sh for Linux/macOS
```

### 2. Configure Clients
Launch two or more instances of the DownloadManager application. In each instance:
1. Go to **Settings**.
2. Set the **P2P Server Port** (default is 5000) and the **P2P Directory** (the folder containing files you wish to share).
3. Ensure the **Central Server Address** is correct (`localhost:5000` for local testing).

### 3. Discover & Download
1. Click the **P2P** button in the main application window.
2. After a "Successfully Connected" notification, click **Refresh** in the P2P tab.
3. A list of available files from other peers will appear. Double-click a file to start downloading it directly from the owner.
