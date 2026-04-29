package com.project.downloadmanager.util.template;

import com.project.downloadmanager.model.DownloadDto;
import com.project.downloadmanager.util.UrlValidator;
import javafx.scene.control.Alert;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import static com.project.downloadmanager.gui.AlertManager.showAlert;

public abstract class AbstractDownloadManager {
    @Getter
    protected final Map<String, DownloadDto> downloads = new ConcurrentHashMap<>();
    protected final ExecutorService executorService = Executors.newCachedThreadPool();

    public DownloadDto handleDownload(String url) {
        if (isValidUrl(url)) {
            if (!downloads.containsKey(url)) {
                return downloadStart(url);
            } else {
                throw new RuntimeException("Таке завантаження вже існує, видаліть його та спробуйте знову");
            }
        } else {
            showAlert(Alert.AlertType.ERROR,"Невалідний URL", "Введений URL не є правильним. Будь ласка, перевірте його.");
            return null;
        }
    }

    private boolean isValidUrl(String url) {
        return UrlValidator.isValidUrl(url);
    }

    protected abstract DownloadDto downloadStart(String url);
    public abstract void resume(String url);
    public abstract void pause(String url);
    public abstract void delete(String url);
}
