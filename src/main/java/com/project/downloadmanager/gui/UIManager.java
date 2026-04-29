package com.project.downloadmanager.gui;

import com.project.downloadmanager.model.DownloadDto;
import com.project.downloadmanager.model.entity.PeerFileInfo;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class UIManager {
    private final ListView<HBox> downloadListView;
    private final ListView<HBox> p2pListView;

    public UIManager(ListView<HBox> downloadListView, ListView<HBox> p2pListView) {
        this.downloadListView = downloadListView;
        this.p2pListView = p2pListView;
    }

    public TextField createUrlField() {
        TextField urlField = new TextField();
        urlField.setPromptText("Введіть URL для завантаження");
        return urlField;
    }

    public Button createStartButton(TextField urlField, EventHandler<ActionEvent> startDownloadHandler, String iconPath) {
        ImageView icon = new ImageView(new Image(iconPath));
        icon.setFitWidth(20);
        icon.setFitHeight(20);
        Button startButton = new Button("", icon);
        startButton.setOnAction(startDownloadHandler);
        urlField.setOnAction(startDownloadHandler);
        return startButton;
    }

    public Button createIconButton(String iconPath, EventHandler<ActionEvent> action) {
        ImageView icon = new ImageView(new Image(iconPath));
        icon.setFitWidth(20);
        icon.setFitHeight(20);
        Button button = new Button("", icon);
        button.setOnAction(action);
        return button;
    }

    public Button createRefreshButton(String iconPath) {
        ImageView icon = new ImageView(new Image(iconPath));
        icon.setFitWidth(20);
        icon.setFitHeight(20);
        Button refreshButton = new Button("", icon);
        refreshButton.setDisable(true);
        return refreshButton;
    }

    public TabPane createTabPane() {
        TabPane tabPane = new TabPane();

        Tab downloadTab = new Tab("Завантаження");
        downloadTab.setClosable(false);
        downloadTab.setContent(downloadListView);

        Tab p2pTab = new Tab("P2P файли");
        p2pTab.setClosable(false);
        p2pTab.setContent(p2pListView);

        tabPane.getTabs().addAll(downloadTab, p2pTab);
        return tabPane;
    }

    public VBox createMainLayout(TextField urlField, HBox buttonBar,
                                 Label p2pStatusLabel, TabPane tabPane) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(
                buttonBar,
                new Label("Введіть URL для завантаження:"),
                urlField,
                p2pStatusLabel,
                tabPane
        );
        return layout;
    }
}

