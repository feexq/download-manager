package com.project.downloadmanager.repo;

import com.project.downloadmanager.model.entity.DownloadStatistic;
import com.project.downloadmanager.model.entity.LogError;
import com.project.downloadmanager.repo.interfaces.LogErrorRepository;
import com.project.downloadmanager.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LogErrorRepositoryImpl implements LogErrorRepository {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    @Override
    public void save(LogError logError) throws SQLException {
        String query = "INSERT INTO log_error (id, download_id, error_message, created_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setLong(1, logError.getId());
            statement.setLong(2, logError.getDownload_id());
            statement.setString(3, logError.getError_message());
            statement.setString(4, logError.getCreated_at().toString());
            statement.executeUpdate();
        }
    }

    @Override
    public List<LogError> findAll() throws SQLException {
        List<LogError> logErrors = new ArrayList<>();
        String query = "SELECT * FROM log_error";
        try (Statement statement = getConnection().createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                LogError logError = new LogError(
                        resultSet.getLong("id"),
                        resultSet.getLong("download_id"),
                        resultSet.getString("error_message"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()
                );
                logErrors.add(logError);
            }
        }
        return logErrors;
    }

    @Override
    public Optional<LogError> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM log_error WHERE download_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    LogError logError = new LogError(
                            rs.getLong("id"),
                            rs.getLong("download_id"),
                            rs.getString("error_message"),
                            parseTimestamp(rs.getString("created_at"))
                    );
                    return Optional.of(logError);
                }
            }
        }

        return Optional.empty();
    }

    private LocalDateTime parseTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(timestamp);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Неможливо розібрати мітку часу: " + timestamp, e);
        }
    }
}
