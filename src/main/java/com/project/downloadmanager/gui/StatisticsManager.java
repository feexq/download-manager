package com.project.downloadmanager.gui;

import com.project.downloadmanager.model.entity.DownloadStatistic;
import com.project.downloadmanager.model.entity.Download;
import com.project.downloadmanager.model.entity.LogError;
import com.project.downloadmanager.model.entity.PeerFileInfo;
import com.project.downloadmanager.service.LogErrorServiceImpl;
import com.project.downloadmanager.service.interfaces.DownloadService;
import com.project.downloadmanager.service.DownloadServiceImpl;
import com.project.downloadmanager.service.interfaces.LogErrorService;
import com.project.downloadmanager.service.mapper.DownloadMapper;
import com.project.downloadmanager.service.DownloadManagerStatistic;
import com.project.downloadmanager.util.composite.DownloadCategory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StatisticsManager {
    private final DownloadManagerStatistic downloadManagerStatistic;
    private final DownloadService downloadService = new DownloadServiceImpl();
    private final DownloadMapper downloadMapper = new DownloadMapper();
    private final LogErrorService logErrorService = new LogErrorServiceImpl();

    public StatisticsManager(DownloadManagerStatistic downloadManagerStatistic) {
        this.downloadManagerStatistic = downloadManagerStatistic;
    }

    public void showStatisticsWindow() {
        Stage statsStage = new Stage();
        statsStage.setTitle("Статистика завантажень");
        Image logoIcon = new Image("logo.png");
        statsStage.getIcons().add(logoIcon);
        DownloadStatistic downloadStatistic;
        try {
            downloadManagerStatistic.calculateStatistics();
            downloadStatistic = downloadManagerStatistic.getStatistic();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

        TableView<Download> pausedDownloads = createDownloadTable(DownloadManagerStatistic.getInstance().getPausedDownloads());
        TableView<Download> completeDownloads = createDownloadTable(DownloadManagerStatistic.getInstance().getCompleteDownloads());
        TableView<Download> failedDownloads = createDownloadTable(DownloadManagerStatistic.getInstance().getFailedDownloads());
        TableView<PeerFileInfo> peerDownloads = createPeerFileTable(DownloadManagerStatistic.getInstance().getPeerDownloads());

        Tab paused = new Tab("Зупинені завантаження");
        paused.setContent(pausedDownloads);
        Tab complete = new Tab("Завершені завантаження");
        complete.setContent(completeDownloads);
        Tab failed = new Tab("Помилка завантаження");
        failed.setContent(failedDownloads);
        Tab peers = new Tab("P2P завантаження");
        peers.setContent(peerDownloads);

        TabPane tabForStatistics = new TabPane();
        tabForStatistics.getTabs().addAll(paused, complete, failed, peers);

        Label clickToViewDetailsLabel = new Label("Клікніть на завантаження, щоб побачити деталі");

        Label totalDownloadsLabel = new Label("Загальна кількість завантажень: " + downloadStatistic.getDownloads());
        Label completedDownloadsLabel = new Label("Кількість завантажених байт: " + downloadStatistic.getDownloadsSize() + " bytes");
        Label daysDownloadLabel = new Label("Кількість днів з моменту першого завантаження: " + downloadStatistic.getDownloadTotalTime() + " днів");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(totalDownloadsLabel, completedDownloadsLabel, daysDownloadLabel, tabForStatistics, clickToViewDetailsLabel);

        Scene scene = new Scene(layout, 600, 400);
        statsStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/static/style.css").toExternalForm());
        statsStage.show();
    }

    private TableView<Download> createDownloadTable(DownloadCategory downloadCategory) {
        TableView<Download> table = new TableView<>();

        TableColumn<Download, String> urlColumn = new TableColumn<>("URL");
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));

        TableColumn<Download, String> statusColumn = new TableColumn<>("Статус");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(urlColumn, statusColumn);

        ObservableList<Download> downloads = mapToObservableList(downloadCategory);
        table.setItems(downloads);

        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showDownloadDetails(newValue);
            }
        });

        return table;
    }

    private void showDownloadDetails(Download download) {
        LogError logError = logErrorService.findById(download.getId());

        String details = "URL: " + download.getUrl() + "\n" +
                "Розмір: " + download.getSize() + " байт\n" +
                "Завантажено: " + download.getDownloaded() + " байт\n" +
                "Час початку: " + download.getStartTime() + "\n" +
                "Час закінчення: " + download.getEndTime() + "\n" +
                "Статус: " + download.getStatus() + "\n\n" +
                "Лог помилки:\n" + (logError != null ? logError : "Лог відсутній");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Детальна інформація про завантаження");
        alert.setHeaderText(null);
        alert.setContentText(details);
        alert.showAndWait();
    }

    private TableView<PeerFileInfo> createPeerFileTable(DownloadCategory peerCategory) {
        TableView<PeerFileInfo> table = new TableView<>();

        TableColumn<PeerFileInfo, String> fileNameColumn = new TableColumn<>("Назва файлу");
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));

        table.getColumns().addAll(fileNameColumn);

        ObservableList<PeerFileInfo> peers;
        try {
            peers = mapPeerCategoryToObservableList(peerCategory);
        } catch (Exception e) {
            peers = FXCollections.observableArrayList();
            System.err.println("Error mapping peer category to observable list: " + e.getMessage());
        }
        table.setItems(peers);

        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showPeerFileDetails(newValue);
            }
        });

        return table;
    }

    private void showPeerFileDetails(PeerFileInfo peerFileInfo) {
        String details = "Назва файлу: " + peerFileInfo.getFileName() + "\n" +
                "Адреса: " + peerFileInfo.getAddress() + "\n" +
                "Порт: " + peerFileInfo.getPort();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Детальна інформація про пір");
        alert.setHeaderText(null);
        alert.setContentText(details);
        alert.showAndWait();
    }

    private ObservableList<PeerFileInfo> mapPeerCategoryToObservableList(DownloadCategory peerCategory) {
        if (peerCategory == null || peerCategory.getDownloads() == null) {
            return FXCollections.observableArrayList();
        }

        return FXCollections.observableArrayList(peerCategory.getPeerFiles());
    }


    private ObservableList<Download> mapToObservableList(DownloadCategory downloadCategory) {
        if (downloadCategory == null || downloadCategory.getDownloads() == null) {
            return FXCollections.observableArrayList();
        }

        return FXCollections.observableArrayList(downloadCategory.getDownloads().stream().map(downloadMapper::toDownload).toList());
    }
}
