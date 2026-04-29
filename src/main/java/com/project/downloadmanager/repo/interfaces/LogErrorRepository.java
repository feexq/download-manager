package com.project.downloadmanager.repo.interfaces;

import com.project.downloadmanager.model.entity.LogError;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface LogErrorRepository {
    List<LogError> findAll() throws SQLException;
    void save(LogError logError) throws SQLException;
    Optional<LogError> findById(Long id) throws SQLException;
}
