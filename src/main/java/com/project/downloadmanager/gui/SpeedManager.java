package com.project.downloadmanager.gui;

import com.project.downloadmanager.DownloadManagerApplication;
import com.project.downloadmanager.config.ConfigLoader;
import com.project.downloadmanager.model.DownloadDto;
import com.project.downloadmanager.model.enums.DownloadStatus;
import com.project.downloadmanager.util.command.Command;
import com.project.downloadmanager.util.command.impl.PauseDownloadCommand;
import com.project.downloadmanager.util.command.impl.ResumeDownloadCommand;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import static com.project.downloadmanager.gui.AlertManager.showAlert;

public class SpeedManager {

    public static double calculateProgress(DownloadDto download) {
        try {
            long totalBytes = download.getSize();
            long downloadedBytes = download.getDownloaded();

            if (totalBytes > 0) {
                return (double) downloadedBytes / totalBytes;
            }
        } catch (Exception e) {
            // Handle exception if progress cannot be calculated
        }

        return ProgressBar.INDETERMINATE_PROGRESS;
    }

    public static void updateDownloadSpeed(DownloadDto download, long[] lastDownloadedBytes, long[] lastTime, Label speedLabel, Label timeLabel) {
        long currentTime = System.currentTimeMillis();
        long currentDownloadedBytes = download.getDownloaded();

        long elapsedTime = currentTime - lastTime[0];
        long downloadedBytes = currentDownloadedBytes - lastDownloadedBytes[0];

        if (elapsedTime > 0) {
            long speed = (downloadedBytes * 1000) / elapsedTime;
            speedLabel.setText("Швидкість: " + formatSpeed(speed));

            long remainingBytes = download.getSize() - currentDownloadedBytes;
            if (speed > 0) {
                long remainingTime = remainingBytes / speed; // seconds
                timeLabel.setText("Час залишився: " + formatTime(remainingTime));
            } else {
                timeLabel.setText("Час залишився: невідомо");
            }
        }

        lastDownloadedBytes[0] = currentDownloadedBytes;
        lastTime[0] = currentTime;
    }

    public static void monitorDownload(DownloadDto download,
                                       Label urlLabel,
                                       ProgressBar progressBar,
                                       Button pauseResumeButton,
                                       Label speedLabel,
                                       Label timeLabel) {
        final long[] lastDownloadedBytes = {download.getDownloaded()};
        final long[] lastTime = {System.currentTimeMillis()};

        Thread progressThread = new Thread(() -> {
            while (true) {
                if (download.getStatus() == null) {
                    break;
                }

                Platform.runLater(() -> {
                    try {
                        double progress = calculateProgress(download);
                        progressBar.setProgress(progress);
                        int percent = (int) (progress * 100);
                        urlLabel.setText(download.getUrl() + " [" + percent + "%]");
                        updateDownloadSpeed(download, lastDownloadedBytes, lastTime, speedLabel, timeLabel);
                    } catch (Exception e) {
                        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                    }

                    switch (download.getStatus()) {
                        case COMPLETED:
                            progressBar.setProgress(1.0);
                            urlLabel.setText(download.getUrl() + " [Завершено]");
                            Platform.runLater(() -> {
                                pauseResumeButton.setText("Найти");
                                pauseResumeButton.setOnAction(event -> {
                                    try {
                                        java.awt.Desktop.getDesktop().open(new java.io.File(ConfigLoader.getDownloadDirectory()));
                                    } catch (Exception e) {
                                        showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося відкрити директорію: " + e.getMessage());
                                    }
                                });
                                urlLabel.setText(download.getUrl() + " [Завершено]");
                            });
                            break;
                        case ERROR:
                            progressBar.setProgress(0);
                            urlLabel.setText(download.getUrl() + " [Помилка]");
                            pauseResumeButton.setDisable(true);
                            break;
                        case PAUSED:
                            urlLabel.setText(download.getUrl() + " [Призупинено]");
                            pauseResumeButton.setText("Продовжити");
                            break;
                    }
                });

                if (download.getStatus() == DownloadStatus.COMPLETED ||
                        download.getStatus() == DownloadStatus.ERROR) {
                    break;
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        progressThread.start();
    }

    public static String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (hours > 0) {
            return String.format("%d год %d хв %d сек", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%d хв %d сек", minutes, secs);
        } else {
            return secs + " сек";
        }
    }

    public static String formatSpeed(long speed) {
        if (speed >= 1024 * 1024) {
            return String.format("%.2f MB/s", speed / (1024.0 * 1024.0));
        } else if (speed >= 1024) {
            return String.format("%.2f KB/s", speed / 1024.0);
        } else {
            return speed + " B/s";
        }
    }

    public static void showSpeedChangeDialog(DownloadDto download, Label speedLabel) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Зміна швидкості");
        dialog.setHeaderText("Введіть нову швидкість завантаження (bytes). Число повинне бути цілим");

        dialog.showAndWait().ifPresent(newSpeed -> {
            try {
                int speed = Integer.parseInt(newSpeed);
                if (speed <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Помилка", "Швидкість повинна бути більшою за 0.");
                    return;
                }
                download.setCustomMaxSpeed(speed);
                download.pause();
                speedLabel.setText("Швидкість: " + SpeedManager.formatSpeed(speed));
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Помилка", "Невірний формат швидкості.");
            }
        });
    }
}

