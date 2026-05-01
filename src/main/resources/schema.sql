CREATE TABLE IF NOT EXISTS downloads (
    id INTEGER PRIMARY KEY,
    url TEXT,
    size INTEGER,
    downloaded INTEGER,
    start_time TEXT,
    end_time TEXT,
    status TEXT
);

CREATE TABLE IF NOT EXISTS peer_file_info (
    id INTEGER PRIMARY KEY,
    fileName TEXT,
    address TEXT,
    port INTEGER
);

CREATE TABLE IF NOT EXISTS log_error (
    id INTEGER PRIMARY KEY,
    download_id INTEGER,
    error_message TEXT,
    created_at TEXT
);

CREATE TABLE IF NOT EXISTS download_statistics (
    id INTEGER PRIMARY KEY,
    downloads INTEGER,
    downloads_size INTEGER,
    download_total_time INTEGER
);
