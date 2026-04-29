package com.project.downloadmanager.service.interfaces;

import com.project.downloadmanager.model.DownloadDto;
import com.project.downloadmanager.model.entity.LogError;

import java.sql.SQLException;
import java.util.Optional;

public interface LogErrorService {
    void save(Exception e, DownloadDto dto);
    LogError findById(Long id);
}
