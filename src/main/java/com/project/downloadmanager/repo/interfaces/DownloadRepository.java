package com.project.downloadmanager.repo.interfaces;

import com.project.downloadmanager.model.entity.Download;
import com.project.downloadmanager.model.enums.DownloadStatus;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DownloadRepository {
    Download save(Download entity) throws SQLException;
    Optional<Download> findById(Long id) throws SQLException;
    List<Download> findAll() throws SQLException;
    void delete(Download entity) throws SQLException;
    void deleteById(Long id) throws SQLException;
    List<Download> findByStatus(DownloadStatus status) throws SQLException;
}
