package com.project.downloadmanager.gui;

import com.project.downloadmanager.DownloadManagerApplication;
import com.project.downloadmanager.config.ConfigLoader;
import com.project.downloadmanager.model.entity.PeerFileInfo;
import com.project.downloadmanager.service.interfaces.PeerFileService;
import com.project.downloadmanager.util.p2p.Peer;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import static com.project.downloadmanager.gui.AlertManager.showAlert;

public class P2PManager {

    private final PeerFileService peerFileService;

    public P2PManager(PeerFileService peerFileServiceImpl) {
        this.peerFileService = peerFileServiceImpl;
    }

    public void connectToP2PServer(Button button, Label label, Button connection, Button stop) {
        try {
            new Peer();
            label.setText("Статус підключення: Підключено");
            button.setDisable(false);
            stop.setDisable(false);
            showAlert(Alert.AlertType.INFORMATION, "Підключено", "Натисність кнопку refresh для перевірки доступних файлів");
            connection.setDisable(true);
        } catch (RuntimeException e) {
            button.setDisable(true);
            connection.setDisable(false);
            label.setText("Статус підключення: Не підключено");
            showAlert(Alert.AlertType.WARNING, "Увага", "Сталася помилка при підключенні до сервера: " + e.getMessage() + " Перевірте правильність адреси та порта серверу");
        }
    }

    public void refreshPeerFiles() {
        try {
            DownloadManagerApplication.getInstance().getP2pListView().getItems().clear();
            List<PeerFileInfo> peerFileInfos = Peer.getAllFiles();
            peerFileInfos.forEach(this::setP2pListView);
            if (peerFileInfos.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "На сервері не найдено жодний файлів", "Файлів не знайдено");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Неможливо найти доступні файли");
        }
    }

    public void setP2pListView(PeerFileInfo peerFileInfo) {
        Label fileName = new Label(peerFileInfo.getFileName() + " " + peerFileInfo.getAddress() + " " + peerFileInfo.getPort());
        HBox p2pItem = new HBox(10);

        Button downloadButton = new Button("Завантажити");
        downloadButton.setPrefWidth(80);

        p2pItem.setPadding(new Insets(10));
        p2pItem.getChildren().addAll(fileName, downloadButton);
        DownloadManagerApplication.getInstance().getP2pListView().getItems().add(p2pItem);

        downloadP2PButton(downloadButton,peerFileInfo);
    }

    public void downloadP2PButton(Button button, PeerFileInfo peerFileInfo) {
        button.setOnAction(event -> {
            try {
                Peer.downloadFileFromPeer(peerFileInfo.getAddress(), peerFileInfo.getPort(), peerFileInfo.getFileName());
                button.setDisable(true);
                peerFileService.save(peerFileInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }});
    }

    public void stopPeerServer(Button button, Label label, Button connection, Button stop) {
        Peer.stopPeerServer();
        button.setDisable(true);
        connection.setDisable(false);
        stop.setDisable(true);
        label.setText("Статус підключення: Не підключено");
    }

}
