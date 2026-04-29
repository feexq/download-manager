package com.project.downloadmanager.util.observer.impl;

import com.project.downloadmanager.DownloadManagerApplication;
import com.project.downloadmanager.model.DownloadDto;
import com.project.downloadmanager.model.entity.LogError;
import com.project.downloadmanager.model.enums.DownloadStatus;
import com.project.downloadmanager.repo.LogErrorRepositoryImpl;
import com.project.downloadmanager.repo.interfaces.LogErrorRepository;
import com.project.downloadmanager.util.composite.DownloadCategory;
import com.project.downloadmanager.util.observer.Observer;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import org.controlsfx.control.Notifications;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class GUIObserver implements Observer {
    @Getter
    private static GUIObserver instance;

    public GUIObserver() {
        instance = this;
    }

    private Map<String, DownloadStatus> lastProcessedStatus = new ConcurrentHashMap<>();

    @Override
    public void update(DownloadDto dto) {
        DownloadStatus currentStatus = dto.getStatus();
        DownloadStatus previousStatus = lastProcessedStatus.get(dto.getUrl());

        if (currentStatus == previousStatus) {
            return;
        }

        lastProcessedStatus.put(dto.getUrl(), currentStatus);

        switch (dto.getStatus()) {
            case DOWNLOADING:
                showNotification("Download Downloading", dto.getUrl() + " is downloading.");
                break;
            case PAUSED:
                showNotification("Download Paused", dto.getUrl() + " is paused.");
                break;
            case COMPLETED:
                showNotification("Download Completed", dto.getUrl() + " has been completed.");
                break;
            case ERROR:
                showNotification("Download Failed", dto.getUrl() + " failed to download.");
                break;
        }
    }

    private void showNotification(String title, String message) {
        Platform.runLater(() -> {
            Notifications.create()
                    .title(title)
                    .text(message)
                    .position(Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(5))
                    .showInformation();
        });
    }


}
