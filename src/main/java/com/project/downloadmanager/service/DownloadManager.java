package com.project.downloadmanager.service;

import com.project.downloadmanager.model.DownloadDto;
import com.project.downloadmanager.model.enums.DownloadStatus;
import com.project.downloadmanager.util.observer.impl.GUIObserver;

import java.util.*;

import com.project.downloadmanager.util.template.AbstractDownloadManager;

public class DownloadManager extends AbstractDownloadManager {

    @Override
    public void resume(String url) {
        DownloadDto download = downloads.get(url);
        if (download == null) {
            System.out.println("No download found for: " + url);
            return;
        }

        if (download.getStatus() != DownloadStatus.PAUSED) {
            System.out.println("Download is not paused for: " + url);
            return;
        }

        System.out.println("Resuming download: " + url);
        if (GUIObserver.getInstance() != null) {
            download.attach(GUIObserver.getInstance());
        } else {
            download.attach(new GUIObserver());
        }
        download.resume();
        executorService.submit(download);
    }

    @Override
    public void pause(String url) {
        DownloadDto download = downloads.get(url.trim());
        if (download == null) {
            System.out.println("No download found for: " + url);
            return;
        }

        if (download.getStatus() != DownloadStatus.DOWNLOADING) {
            System.out.println("Download is not active for: " + url);
            return;
        }

        System.out.println("Pausing download: " + url);
        download.pause();
    }

    @Override
    public void delete(String url) {
        DownloadDto download = downloads.get(url);
        if (download == null) {
            System.out.println("No download to cancel for: " + url);
            return;
        }

        System.out.println("Cancelling download: " + url);
        download.setStatus(DownloadStatus.CANCELLED);
        downloads.remove(url);
    }

    @Override
    public DownloadDto downloadStart(String url) {

        System.out.println("Starting download: " + url);
        DownloadDto download = new DownloadDto(url);

        if (GUIObserver.getInstance() != null) {
            download.attach(GUIObserver.getInstance());
        } else {
            download.attach(new GUIObserver());
        }

        downloads.put(url, download);
        executorService.submit(download);
        return download;
    }
}
