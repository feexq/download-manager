package com.project.downloadmanager;

import com.project.downloadmanager.config.ConfigLoader;
import com.project.downloadmanager.gui.*;
import com.project.downloadmanager.model.DownloadDto;
import com.project.downloadmanager.model.enums.DownloadStatus;
import com.project.downloadmanager.service.PeerFileServiceImpl;
import com.project.downloadmanager.service.interfaces.DownloadService;
import com.project.downloadmanager.service.DownloadServiceImpl;
import com.project.downloadmanager.service.interfaces.PeerFileService;
import com.project.downloadmanager.service.mapper.DownloadMapper;
import com.project.downloadmanager.service.DownloadManager;
import com.project.downloadmanager.service.DownloadManagerStatistic;
import com.project.downloadmanager.util.command.CommandInvoker;
import com.project.downloadmanager.util.p2p.Peer;
import com.project.downloadmanager.util.template.AbstractDownloadManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import static com.project.downloadmanager.gui.AlertManager.showAlert;

@Getter
@Setter
public class DownloadManagerApplication extends Application {
    private final AbstractDownloadManager abstractDownloadManager = new DownloadManager();
    private final CommandInvoker commandInvoker = new CommandInvoker();
    private ListView<HBox> downloadListView = new ListView<>();
    private ListView<HBox> p2pListView = new ListView<>();
    private final DownloadService downloadService = new DownloadServiceImpl();
    private final DownloadMapper downloadMapper = new DownloadMapper();
    private final DownloadManagerStatistic downloadManagerStatistic = new DownloadManagerStatistic();
    private SettingsManager settingsManager;
    private StatisticsManager statisticsManager;
    private PeerFileService peerFileService = new PeerFileServiceImpl();
    private UIDownload uidDownload = new UIDownload(downloadService, downloadMapper, abstractDownloadManager);
    private UIManager uiManager = new UIManager(downloadListView, p2pListView);
    private P2PManager p2PManager = new P2PManager(peerFileService);

    @Getter
    private static DownloadManagerApplication instance;

    public DownloadManagerApplication() {
        instance = this;
    }

    @Override
    public void start(Stage primaryStage) {
        initializeManagers();

        primaryStage.setTitle("Download Manager");
        Image logoIcon = new Image("logo.png");
        primaryStage.getIcons().add(logoIcon);

        TextField urlField = uiManager.createUrlField();
        Button startButton = uiManager.createStartButton(urlField, event -> startDownload(urlField), "download.png");
        startButton.getStyleClass().add("button");
        Button settingsButton = uiManager.createIconButton("settings.png", event -> settingsManager.showSettingsWindow());
        Button statsButton = uiManager.createIconButton("statistics.png", event -> statisticsManager.showStatisticsWindow());
        ImageView iconStop = new ImageView(new Image("square.png"));
        iconStop.setFitWidth(20);
        iconStop.setFitHeight(20);
        Button stopButton = new Button(" ", iconStop);
        stopButton.setDisable(true);
        ImageView icon = new ImageView(new Image("p2p.png"));
        icon.setFitWidth(20);
        icon.setFitHeight(20);
        Button connectButton = new Button("", icon);
        Button refreshButton = uiManager.createRefreshButton("refresh.png");

        HBox buttonBar = new HBox(10,startButton, statsButton, settingsButton, refreshButton, connectButton, stopButton);

        TabPane tabPane = uiManager.createTabPane();
        Label p2pStatusLabel = new Label("Статус підключення: Не підключено");

        setupButtonActions(urlField, startButton, refreshButton, connectButton, p2pStatusLabel, stopButton);

        VBox layout = uiManager.createMainLayout(urlField, buttonBar, p2pStatusLabel, tabPane);
        Scene scene = new Scene(layout, 685, 500);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/static/style.css").toExternalForm());

        initializeDownloadManagerApi();
        setupPrimaryStageCloseHandler(primaryStage);
        uidDownload.loadPausedDownloads();

        primaryStage.show();
    }

    private void initializeManagers() {
        settingsManager = new SettingsManager();
        statisticsManager = new StatisticsManager(downloadManagerStatistic);
    }

    private void setupButtonActions(TextField urlField, Button startButton, Button refreshButton, Button connectButton, Label p2pStatusLabel, Button stopButton) {
        refreshButton.setOnAction(event -> p2PManager.refreshPeerFiles());
        connectButton.setOnAction(event -> p2PManager.connectToP2PServer(refreshButton, p2pStatusLabel, connectButton, stopButton));
        stopButton.setOnAction(event -> p2PManager.stopPeerServer(refreshButton, p2pStatusLabel, connectButton, stopButton));
    }

    private void initializeDownloadManagerApi() {
        try {
            DownloadManagerApi.start();
        } catch (Exception e) {
            System.out.println("Проблеми при запуску API. Перевірте чи port 8080 вільний");
        }
    }

    private void setupPrimaryStageCloseHandler(Stage primaryStage) {
        primaryStage.setOnCloseRequest(event -> {
            if (abstractDownloadManager.getDownloads().values().stream().anyMatch(download -> download.getStatus() == DownloadStatus.DOWNLOADING)) {
                showAlert(Alert.AlertType.WARNING, "Увага", "Є активні завантаження. Завершіть їх перед виходом.");
                event.consume();
            } else {
                uidDownload.saveDownloads();
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void getUrlForDownloadFromExtension(String url) {
        TextField urlField = new TextField(url);
        startDownload(urlField);
    }

    private void startDownload(TextField urlField) {
        String url = urlField.getText().trim();
        if (url.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "URL не може бути порожнім!");
            return;
        }
        try {
            DownloadDto download = abstractDownloadManager.handleDownload(url);
            uidDownload.addDownloadToList(download);
            urlField.clear();
        } catch (Exception e) {
            System.out.println("Помилка валідації");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}