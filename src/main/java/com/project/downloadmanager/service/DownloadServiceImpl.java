package com.project.downloadmanager.service;

import com.project.downloadmanager.model.entity.Download;
import com.project.downloadmanager.model.enums.DownloadStatus;
import com.project.downloadmanager.repo.DownloadRepositoryImpl;
import com.project.downloadmanager.repo.interfaces.DownloadRepository;
import com.project.downloadmanager.service.interfaces.DownloadService;

import java.util.List;

public class DownloadServiceImpl implements DownloadService {
    private final DownloadRepository repository = new DownloadRepositoryImpl();

    public void save(Download download) {
        try {
            repository.save(download);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Download> findAllByStatus(DownloadStatus status) {
        try {
            return repository.findByStatus(status);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Download> findAll() {
        try {
            return repository.findAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
