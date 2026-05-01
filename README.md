# DownloadManager

DownloadManager is a feature-rich desktop utility built with **JavaFX 21** and **Java 17**, designed for efficient file management and high-speed transfers. It utilizes a **custom speed distribution algorithm** to dynamically allocate bandwidth across concurrent downloads, ensuring optimal network usage. Beyond standard HTTP downloads, the system features a **built-in local server** for seamless browser integration and a **decentralized P2P network** for direct file sharing between peers in a **Local Area Network (LAN)**.

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![JavaFX](https://img.shields.io/badge/JavaFX-21.0.4-blue.svg)

## ⭐ Key Features

- **Dynamic Speed Allocation** — Automatically balances a global bandwidth limit across all active downloads.
- **Browser Integration** — Receives links from Chrome/Mozilla extensions via a local HTTP server.
- **Local Network P2P** — Discover and download shared files directly from other peers in a LAN environment.
- **Resumable Downloads** — Support for HTTP Range requests to pause and resume transfers.
- **History & Stats** — Persistent tracking of download metrics stored in a local **SQLite** database.

## 🚀 Quick Start

1. **Run Application**:
   ```bash
   ./scripts/start.bat # Windows or ./scripts/start.sh for Linux/macOS
   ```
   *(This script uses Maven Wrapper and will automatically build the project on first run. No manual Maven installation required!)*

2. **Advanced Features**:
   To set up browser extensions or the P2P network, see the [Advanced Setup Guide](ADVANCED_SETUP.md).

## 🛠 Tech Stack
<details>
<summary><b>View detailed technology layers</b></summary>

- **Core**: Java 17, JavaFX 21, Lombok
- **Data**: SQLite JDBC 3.47, Jakarta Persistence API (JPA) 3.2
- **Patterns**: Observer, Command, Template Method, Iterator, Composite

</details>

## 📸 Screenshots
<details>
<summary><b>View application gallery</b></summary>

<p align="center">
  <img src="assets/screenshot_1.png" width="30%" alt="Main" />
  <img src="assets/screenshot_3.png" width="30%" alt="Stats" />
  <img src="assets/screenshot_2.png" width="30%" alt="P2P" />
</p>

</details>
