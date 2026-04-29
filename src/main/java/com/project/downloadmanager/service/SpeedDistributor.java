package com.project.downloadmanager.service;

import com.project.downloadmanager.config.ConfigLoader;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SpeedDistributor {
    private static final SpeedDistributor INSTANCE = new SpeedDistributor();
    private final Map<String, DownloadSpeed> activeDownloads = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean redistributionPending = false;

    @Getter
    private static class DownloadSpeed {
        private volatile int allocatedSpeed;
        private final int personalLimit;

        public DownloadSpeed(int personalLimit) {
            this.personalLimit = personalLimit;
        }
    }

    private SpeedDistributor() {
        executor.scheduleAtFixedRate(this::redistributeSpeed, 100, 200, TimeUnit.MILLISECONDS);
    }

    public static SpeedDistributor getInstance() {
        return INSTANCE;
    }

    public synchronized void registerDownload(String url, int personalLimit) {
        activeDownloads.put(url, new DownloadSpeed(personalLimit));
        redistributionPending = true;
    }

    public synchronized void unregisterDownload(String url) {
        activeDownloads.remove(url);
        redistributionPending = true;
    }

    private void redistributeSpeed() {
        if (!redistributionPending) {
            return;
        }

        synchronized (this) {
            int globalLimit = ConfigLoader.getMaxDownloadSpeed();
            if (globalLimit <= 0 || activeDownloads.isEmpty()) {
                for (DownloadSpeed speed : activeDownloads.values()) {
                    speed.allocatedSpeed = 0;
                }
                redistributionPending = false;
                return;
            }

            int remainingSpeed = globalLimit;
            int remainingDownloads = activeDownloads.size();

            for (DownloadSpeed speed : activeDownloads.values()) {
                if (remainingSpeed <= 0) {
                    speed.allocatedSpeed = 0;
                    continue;
                }

                int allocation = Math.min(remainingSpeed / remainingDownloads, speed.personalLimit);
                speed.allocatedSpeed = allocation;
                remainingSpeed -= allocation;
                remainingDownloads--;
            }

            if (remainingSpeed > 0 && !activeDownloads.isEmpty()) {
                for (DownloadSpeed speed : activeDownloads.values()) {
                    if (remainingSpeed <= 0) {
                        break;
                    }

                    int additionalAllocation = Math.min(remainingSpeed, speed.personalLimit - speed.allocatedSpeed);
                    speed.allocatedSpeed += additionalAllocation;
                    remainingSpeed -= additionalAllocation;
                }
            }

            redistributionPending = false;
        }
    }


    public int getAllocatedSpeed(String url) {
        DownloadSpeed speed = activeDownloads.get(url);
        return speed != null ? speed.allocatedSpeed : 0;
    }

    public void shutdown() {
        executor.shutdown();
    }
}
