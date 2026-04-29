package com.project.downloadmanager.gui;

import com.project.downloadmanager.DownloadManagerApplication;
import com.project.downloadmanager.model.DownloadDto;
import com.project.downloadmanager.model.enums.DownloadStatus;
import com.project.downloadmanager.service.DownloadServiceImpl;
import com.project.downloadmanager.service.interfaces.DownloadService;
import com.project.downloadmanager.service.mapper.DownloadMapper;
import com.project.downloadmanager.util.command.Command;
import com.project.downloadmanager.util.command.impl.PauseDownloadCommand;
import com.project.downloadmanager.util.command.impl.ResumeDownloadCommand;
import com.project.downloadmanager.util.iterator.Aggregate;
import com.project.downloadmanager.util.iterator.Iterator;
import com.project.downloadmanager.util.iterator.impl.DownloadAggregateImpl;
import com.project.downloadmanager.util.template.AbstractDownloadManager;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.List;

import static com.project.downloadmanager.gui.AlertManager.showAlert;

public class UIDownload {

    private final DownloadService downloadService;
    private final DownloadMapper downloadMapper;
    private final AbstractDownloadManager downloadManager;

    public UIDownload(DownloadService downloadService, DownloadMapper downloadMapper, AbstractDownloadManager downloadManager) {
        this.downloadService = downloadService;
        this.downloadMapper = downloadMapper;
        this.downloadManager = downloadManager;
    }

    public void saveDownloads() {
        List<DownloadDto> activeDownloads = downloadManager.getDownloads().values().stream()
                .filter(download -> download.getStatus() == DownloadStatus.DOWNLOADING)
                .toList();
        if (!activeDownloads.isEmpty()) {
            Platform.runLater(() -> showAlert(Alert.AlertType.WARNING, "Увага",
                    "Є активні завантаження. Перевірте їх перед закриттям програми."));
            return;
        }
        if (!DownloadManagerApplication.getInstance().getDownloadListView().getItems().isEmpty()) {
            try {
                Aggregate<DownloadDto> aggregate = new DownloadAggregateImpl(downloadManager.getDownloads().values().stream().toList());
                Iterator<DownloadDto> iterator = aggregate.createIterator();
                while (iterator.hasNext()) {
                    DownloadDto downloadDto = iterator.next();
                    downloadService.save(downloadMapper.toDownload(downloadDto));
                }
                showAlert(Alert.AlertType.INFORMATION, "Успіх", "Завантаження збережено.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося зберегти завантаження: " + e.getMessage());
            }
        }
    }

    public void addDownloadToList(DownloadDto download) {
        Label urlLabel = new Label(download.getUrl());
        urlLabel.setPrefWidth(250);
        HBox.setHgrow(urlLabel, Priority.ALWAYS);

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);
        HBox.setHgrow(progressBar, Priority.ALWAYS);

        Label speedLabel = new Label("Швидкість: 0 B/s");

        Label timeLabel = new Label("Часу залишлось: ");

        Button pauseResumeButton = new Button("Пауза");
        pauseResumeButton.setPrefWidth(80);

        HBox downloadItem = new HBox(10);
        downloadItem.getChildren().addAll(urlLabel, progressBar, speedLabel, timeLabel ,pauseResumeButton);

        DownloadManagerApplication.getInstance().getDownloadListView().getItems().add(downloadItem);

        setupPauseResumeButton(download, pauseResumeButton, urlLabel, progressBar);

        ContextMenu contextMenu = new ContextMenu();

        MenuItem changeSpeedItem = new MenuItem("Змінити швидкість");
        MenuItem deleteDownload = new MenuItem("Видалити завантаження");
        changeSpeedItem.setOnAction(event -> SpeedManager.showSpeedChangeDialog(download, speedLabel));
        deleteDownload.setOnAction(event -> deleteAction(download));
        contextMenu.getItems().addAll(changeSpeedItem, deleteDownload);

        downloadItem.setOnContextMenuRequested(event -> contextMenu.show(downloadItem, event.getScreenX(), event.getScreenY()));

        SpeedManager.monitorDownload(download, urlLabel, progressBar, pauseResumeButton, speedLabel, timeLabel);
    }

    public void deleteAction(DownloadDto download) {
        try {
            DownloadManagerApplication.getInstance().getAbstractDownloadManager().delete(download.getUrl());
            DownloadManagerApplication.getInstance().getDownloadListView().getItems().remove(DownloadManagerApplication.getInstance().getDownloadListView().getSelectionModel().getSelectedItem());
            DownloadManagerApplication.getInstance().getDownloadService().delete(download.getId());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Помилка під час видалення завантаження");
        }
    }

    public void setupPauseResumeButton(DownloadDto download,
                                       Button pauseResumeButton,
                                       Label urlLabel,
                                       ProgressBar progressBar) {
        pauseResumeButton.setOnAction(event -> {
            try {
                if (download.getStatus() == DownloadStatus.DOWNLOADING) {
                    Command pauseCommand = new PauseDownloadCommand(DownloadManagerApplication.getInstance().getAbstractDownloadManager(), download.getUrl());
                    DownloadManagerApplication.getInstance().getCommandInvoker().setCommand(pauseCommand);
                    DownloadManagerApplication.getInstance().getCommandInvoker().executeCommand();
                    pauseResumeButton.setText("Продовжити");
                    urlLabel.setText(download.getUrl() + " [Призупинено]");
                } else if (download.getStatus() == DownloadStatus.PAUSED) {
                    Command resumeCommand = new ResumeDownloadCommand(DownloadManagerApplication.getInstance().getAbstractDownloadManager(),download.getUrl());
                    DownloadManagerApplication.getInstance().getCommandInvoker().setCommand(resumeCommand);
                    DownloadManagerApplication.getInstance().getCommandInvoker().executeCommand();
                    pauseResumeButton.setText("Пауза");
                    urlLabel.setText(download.getUrl());
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Помилка",
                        "Не вдалося змінити статус завантаження: " + e.getMessage());
            }
        });
    }

    public void loadPausedDownloads() {
        try {
            List<DownloadDto> downloadDtos = downloadService.findAllByStatus(DownloadStatus.PAUSED)
                    .stream()
                    .map(downloadMapper::toDownloadDto)
                    .toList();
            Aggregate<DownloadDto> downloadDtoAggregate = new DownloadAggregateImpl(downloadDtos);
            Iterator<DownloadDto> downloadDtoIterator = downloadDtoAggregate.createIterator();
            while (downloadDtoIterator.hasNext()) {
                DownloadDto downloadDto = downloadDtoIterator.next();
                addDownloadToList(downloadDto);
                downloadManager.getDownloads().put(downloadDto.getUrl(), downloadDto);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
