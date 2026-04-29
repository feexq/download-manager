package com.project.downloadmanager.service.interfaces;

import com.project.downloadmanager.model.entity.Download;
import com.project.downloadmanager.model.enums.DownloadStatus;

import java.util.List;

public interface DownloadService {
    void save(Download download);
    List<Download> findAllByStatus(DownloadStatus status);
    void delete(Long id);
    List<Download> findAll();
}
