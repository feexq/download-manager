package com.project.downloadmanager.repo.interfaces;

import com.project.downloadmanager.model.entity.DownloadStatistic;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DownloadStatisticRepository {

    Optional<DownloadStatistic> findById(Long id) throws SQLException;

    List<DownloadStatistic> findAll() throws SQLException;

    DownloadStatistic save(DownloadStatistic statistic) throws SQLException;

    void deleteById(Long id) throws SQLException;

    void delete(DownloadStatistic downloadStatistic) throws SQLException;

}
