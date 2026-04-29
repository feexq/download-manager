package com.project.downloadmanager.service;

import com.project.downloadmanager.model.DownloadDto;
import com.project.downloadmanager.model.entity.LogError;
import com.project.downloadmanager.repo.LogErrorRepositoryImpl;
import com.project.downloadmanager.repo.interfaces.LogErrorRepository;
import com.project.downloadmanager.service.interfaces.LogErrorService;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class LogErrorServiceImpl implements LogErrorService {

    LogErrorRepository logErrorRepository = new LogErrorRepositoryImpl();

    public void save(Exception e, DownloadDto dto) {
        LogError logError = new LogError();
        logError.setError_message(e.getMessage());
        logError.setDownload_id(dto.getId());
        logError.setCreated_at(LocalDateTime.now());
        try {
            logErrorRepository.save(logError);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public LogError findById(Long id){
        try {
            return logErrorRepository.findById(id).orElse(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}
