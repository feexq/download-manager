package com.project.downloadmanager.model;

import com.project.downloadmanager.config.ConfigLoader;
import com.project.downloadmanager.model.enums.DownloadStatus;
import com.project.downloadmanager.service.interfaces.LogErrorService;
import com.project.downloadmanager.service.LogErrorServiceImpl;
import com.project.downloadmanager.service.SpeedDistributor;
import com.project.downloadmanager.util.composite.DownloadGroup;
import com.project.downloadmanager.util.observer.Observer;
import com.project.downloadmanager.util.observer.Subject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class DownloadDto implements Runnable, Subject, DownloadGroup {

    private final List<Observer> observers = new ArrayList<>();
    private LogErrorService logErrorService = new LogErrorServiceImpl();

    Random random = new Random();

    private long id = Math.abs(random.nextLong());
    private String url;

    private long size;
    private long downloaded;

    private DownloadStatus status;

    private Date startTime;
    private Date endTime;
    private float speed;
    @Setter
    private Integer customMaxSpeed;

    private final AtomicBoolean pause = new AtomicBoolean(false);
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private FileOutputStream fos;
    private HttpURLConnection huc;

    public DownloadDto(long id, String url, long size,
                       Date startTime , DownloadStatus status, Date endTime) {
        this.id = id;
        this.url = url;
        this.size = size;
        this.startTime = startTime;
        this.status = status;
        this.endTime = endTime;
    }

    public DownloadDto(String url){
        this.url = url;
    }

    @Override
    public void run() {
        try {
            int personalLimit = customMaxSpeed != null ? customMaxSpeed : ConfigLoader.getMaxDownloadSpeed();
            SpeedDistributor.getInstance().registerDownload(url, personalLimit);

            setStatus(DownloadStatus.DOWNLOADING);
            setStartTime(new Date());
            notifyObservers();

            URL urlObj = new URL(getUrl());
            String downloadDirectory = ConfigLoader.getDownloadDirectory();
            String fileName = new File(urlObj.getPath()).getName();
            String filePath = downloadDirectory + File.separator + fileName;

            File directory = new File(downloadDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            if (downloaded == 0) {
                huc = (HttpURLConnection) urlObj.openConnection();
                setSize(huc.getContentLengthLong());
                bis = new BufferedInputStream(huc.getInputStream());
                fos = new FileOutputStream(filePath);
                bos = new BufferedOutputStream(fos);
            } else {
                huc = (HttpURLConnection) urlObj.openConnection();
                huc.setRequestProperty("Range", "bytes=" + downloaded + "-");
                bis = new BufferedInputStream(huc.getInputStream());
                fos = new FileOutputStream(filePath, true);
                bos = new BufferedOutputStream(fos);
            }

            byte[] buffer = new byte[1024];
            int read;
            long startTimeMillis = System.currentTimeMillis();
            long bytesReadSinceLastCheck = 0;

            while ((read = bis.read(buffer, 0, buffer.length)) >= 0) {
                if (status == DownloadStatus.CANCELLED) {
                    notifyObservers();
                    cleanup();
                    return;
                }

                if (pause.get()) {
                    setStatus(DownloadStatus.PAUSED);
                    notifyObservers();
                    cleanup();
                    return;
                }

                bos.write(buffer, 0, read);
                setDownloaded(getDownloaded() + read);
                bytesReadSinceLastCheck += read;

                if (getCustomMaxSpeed() > 0) {
                    long elapsedMillis = System.currentTimeMillis() - startTimeMillis;
                    if (elapsedMillis > 0) {
                        long expectedBytesPerSecond = (bytesReadSinceLastCheck * 1000) / elapsedMillis;
                        if (expectedBytesPerSecond > getCustomMaxSpeed()) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                }

                long elapsedMillis = System.currentTimeMillis() - startTimeMillis;
                setSpeed((float) (getDownloaded() / (elapsedMillis / 1000.0)));
            }

            setEndTime(new Date());
            setStatus(DownloadStatus.COMPLETED);
            notifyObservers();
            cleanup();

        } catch (Exception e) {
            setStatus(DownloadStatus.ERROR);
            logErrorService.save(e,this);
            notifyObservers();
        } finally {
            SpeedDistributor.getInstance().unregisterDownload(url);
            cleanup();
        }
    }

    private void cleanup() {
        try {
            if (bos != null) bos.close();
            if (bis != null) bis.close();
            if (fos != null) fos.close();
            if (huc != null) huc.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        pause.set(true);
    }

    public void resume() {
        pause.set(false);
    }

    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for(Observer observer : observers) {
            observer.update(this);
        }
    }

    @Override
    public void display() {
        System.out.println("Download: " + url);
    }

    public int getCustomMaxSpeed() {
        return SpeedDistributor.getInstance().getAllocatedSpeed(url);
    }
}
