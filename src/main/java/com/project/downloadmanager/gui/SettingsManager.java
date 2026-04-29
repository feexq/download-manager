package com.project.downloadmanager.gui;

import com.project.downloadmanager.config.ConfigLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.function.BiConsumer;

public class SettingsManager {

    public void showSettingsWindow() {
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Налаштування");
        Image logoIcon = new Image("logo.png");
        settingsStage.getIcons().add(logoIcon);

        TextField downloadDirectoryField = new TextField(ConfigLoader.getDownloadDirectory());
        downloadDirectoryField.setPromptText("Директорія для збереження файлів");

        Button chooseDirectoryButton = new Button("Вибрати директорію");
        chooseDirectoryButton.setOnAction(event -> chooseDownloadDirectory(downloadDirectoryField));

        TextField maxSpeedField = new TextField(String.valueOf(ConfigLoader.getMaxDownloadSpeed()));
        maxSpeedField.setPromptText("Максимальна швидкість завантаження (bytes)");

        TextField p2pDirectoryField = new TextField(ConfigLoader.getP2PDirectory());
        p2pDirectoryField.setPromptText("Директорія для p2p обміну файлами");

        TextField p2pPortField = new TextField(String.valueOf(ConfigLoader.getP2PPort()));
        p2pPortField.setPromptText("Порт який буде використовуватись для підключення до серверу p2p");

        TextField p2pServerAddressField = new TextField(ConfigLoader.getP2PServerAddress());
        p2pServerAddressField.setPromptText("IP-адрес центрального серверу");

        TextField p2pCentralServerPortField = new TextField(String.valueOf(ConfigLoader.getCentralPort()));
        p2pServerAddressField.setPromptText("Порт центрального серверу");

        Button chooseP2PDirectoryButton = new Button("Вибрати директорію для P2P");
        chooseP2PDirectoryButton.setOnAction(event -> chooseDownloadDirectory(p2pDirectoryField));

        Button saveButton = new Button("Зберегти");
        saveButton.setOnAction(event -> saveSettings(downloadDirectoryField.getText(), maxSpeedField.getText(), p2pDirectoryField.getText()
                , p2pPortField.getText(), p2pServerAddressField.getText(), p2pCentralServerPortField.getText(), settingsStage));

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(
                new Label("Налаштування завантаження:"),
                new Label("Директорія для збереження:"),
                downloadDirectoryField,
                chooseDirectoryButton,
                new Label("Максимальна швидкість завантаження (bytes):"),
                maxSpeedField,
                new Label("P2P Налаштування:"),
                new Label("Директорія для P2P:"),
                p2pDirectoryField,
                chooseP2PDirectoryButton,
                new Label("Порт P2P сервера:"),
                p2pPortField,
                new Label("Адреса центрального P2P сервера:"),
                p2pServerAddressField,
                new Label("Порт центрального серверу"),
                p2pCentralServerPortField,
                saveButton
        );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene scene = new Scene(scrollPane, 500, 600);
        scene.getStylesheets().add(getClass().getResource("/static/style.css").toExternalForm());
        settingsStage.setScene(scene);
        settingsStage.show();
    }

    private void chooseDownloadDirectory(TextField downloadDirectoryField) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Вибір директорії для збереження");
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            downloadDirectoryField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void saveSettings(String downloadDirectory, String maxSpeed, String p2pDirectoryField, String p2pPortField,
                              String p2pServerAddressField, String centralPort, Stage settingsStage) {
        try {

            int speed = Integer.parseInt(maxSpeed);
            if (speed <= 0) {
                showAlert(Alert.AlertType.ERROR, "Помилка", "Максимальна швидкість повинна бути більше 0.");
                return;
            }
            int port = Integer.parseInt(p2pPortField);
            if (port <= 0) {
                showAlert(Alert.AlertType.ERROR, "Помилка", "Порт не може бути нулем");
                return;
            }
            int central = Integer.parseInt(centralPort);
            if (central <= 0) {
                showAlert(Alert.AlertType.ERROR, "Помилка", "Порт не може бути нулем");
                return;
            }

            ConfigLoader.saveConfig(downloadDirectory, speed, p2pDirectoryField, port, p2pServerAddressField, central);

            settingsStage.close();
            showAlert(Alert.AlertType.INFORMATION, "Успіх", "Налаштування успішно збережено.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Невірний формат швидкості.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
