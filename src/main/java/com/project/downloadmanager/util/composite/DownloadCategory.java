package com.project.downloadmanager.util.composite;


import com.project.downloadmanager.DownloadManagerApplication;
import com.project.downloadmanager.model.DownloadDto;
import com.project.downloadmanager.model.entity.Download;
import com.project.downloadmanager.model.entity.PeerFileInfo;
import com.project.downloadmanager.model.enums.DownloadStatus;
import com.project.downloadmanager.service.mapper.DownloadMapper;
import com.project.downloadmanager.util.iterator.Aggregate;
import com.project.downloadmanager.util.iterator.Iterator;
import com.project.downloadmanager.util.iterator.impl.DownloadAggregateImpl;
import com.project.downloadmanager.util.iterator.impl.DownloadIteratorImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class DownloadCategory implements DownloadGroup{
    private String name;

    private final DownloadMapper downloadMapper = new DownloadMapper();

    private List<DownloadGroup> downloads = new ArrayList<>();

    public DownloadCategory(String name) {
        this.name = name;
    }

    public void addAll(List<DownloadGroup> downloadGroups) {
        this.downloads.addAll(downloadGroups);
    }

    public void add(DownloadGroup component) {
        downloads.add(component);
    }

    public List<DownloadDto> getDownloads() {
        List<DownloadDto> downloadDtos = new ArrayList<>();
        for (DownloadGroup downloadGroup : downloads) {
            if (downloadGroup instanceof DownloadDto) {
                downloadDtos.add((DownloadDto) downloadGroup);
            }
        }
        return downloadDtos;
    }

    public List<DownloadGroup> getActiveDownloadGroups(List<Download> downloadList) {
        downloadList.stream()
                .filter(download -> DownloadStatus.DOWNLOADING.equals(download.getStatus()))
                .map(downloadMapper::toDownloadDto)
                .forEach(this::add);
        return downloads;
    }

    public List<DownloadGroup> getPausedDownloadGroups(List<Download> downloadList) {
        downloadList.stream()
                .filter(download -> DownloadStatus.PAUSED.equals(download.getStatus()))
                .map(downloadMapper::toDownloadDto)
                .forEach(this::add);
        return downloads;
    }

    public List<DownloadGroup> getCompletedDownloadGroups(List<Download> downloadList) {
        downloadList.stream()
                .filter(download -> DownloadStatus.COMPLETED.equals(download.getStatus()))
                .map(downloadMapper::toDownloadDto)
                .forEach(this::add);
        return downloads;
    }

    public List<DownloadGroup> getErrorDownloadGroups(List<Download> downloadList) {
        downloadList.stream()
                .filter(download -> DownloadStatus.ERROR.equals(download.getStatus()))
                .map(downloadMapper::toDownloadDto)
                .forEach(this::add);
        return downloads;
    }

    public List<DownloadGroup> getPeerDownloadGroups(List<PeerFileInfo> peerFileInfos) {
        peerFileInfos.forEach(this::add);
        return downloads;
    }

    public List<PeerFileInfo> getPeerFiles() {
        List<PeerFileInfo> peerFileInfos = new ArrayList<>();
        for (DownloadGroup downloadGroup : downloads) {
            if (downloadGroup instanceof PeerFileInfo) {
                peerFileInfos.add((PeerFileInfo) downloadGroup);
            }
        }
        return peerFileInfos;
    }

    public void remove(DownloadGroup component) {
        downloads.remove(component);
    }

    public void removeAll(DownloadGroup component) {
        while (downloads.contains(component)) {
            remove(component);
        }
    }

    public boolean existsDownload(DownloadGroup component) {
        return downloads.contains(component);
    }

    @Override
    public void display() {
        System.out.println("Category: " + name);
        for (DownloadGroup download : downloads) {
            download.display();
        }
    }
}
