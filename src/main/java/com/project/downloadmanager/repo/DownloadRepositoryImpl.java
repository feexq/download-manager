package com.project.downloadmanager.repo;

import com.project.downloadmanager.model.entity.Download;
import com.project.downloadmanager.model.enums.DownloadStatus;
import com.project.downloadmanager.repo.interfaces.DownloadRepository;
import com.project.downloadmanager.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DownloadRepositoryImpl implements DownloadRepository {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    @Override
    public Download save(Download entity) throws SQLException {
        String checkIfExistsSql = "SELECT COUNT(*) FROM downloads WHERE id = ?";
        String insertSql = "INSERT INTO downloads (id, url, size, downloaded, start_time, end_time, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE downloads SET url = ?, size = ?, downloaded = ?, start_time = ?, end_time = ?, status = ? WHERE id = ?";

        try (Connection connection = getConnection()) {
            boolean exists;

            // Check if the record exists
            try (PreparedStatement checkStatement = connection.prepareStatement(checkIfExistsSql)) {
                checkStatement.setLong(1, entity.getId());
                ResultSet resultSet = checkStatement.executeQuery();
                resultSet.next();
                exists = resultSet.getInt(1) > 0;
            }

            if (exists) {
                // Update existing record
                try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                    updateStatement.setString(1, entity.getUrl());
                    updateStatement.setLong(2, entity.getSize());
                    updateStatement.setLong(3, entity.getDownloaded());
                    updateStatement.setString(4, entity.getStartTime());
                    updateStatement.setString(5, entity.getEndTime());
                    updateStatement.setString(6, entity.getStatus().name());
                    updateStatement.setLong(7, entity.getId());

                    int rowsAffected = updateStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        return entity;
                    } else {
                        throw new SQLException("Failed to update record with id " + entity.getId());
                    }
                }
            } else {
                // Insert new record
                try (PreparedStatement insertStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    insertStatement.setLong(1, entity.getId());
                    insertStatement.setString(2, entity.getUrl());
                    insertStatement.setLong(3, entity.getSize());
                    insertStatement.setLong(4, entity.getDownloaded());
                    insertStatement.setString(5, entity.getStartTime());
                    insertStatement.setString(6, entity.getEndTime());
                    insertStatement.setString(7, entity.getStatus().name());

                    int rowsAffected = insertStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        return entity;
                    } else {
                        throw new SQLException("Failed to insert record.");
                    }
                }
            }
        }
    }

    @Override
    public Optional<Download> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM downloads WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Download download = mapToDownloadDto(resultSet);
                return Optional.of(download);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Download> findByStatus(DownloadStatus status) throws SQLException {
        List<Download> downloads = new ArrayList<>();
        String sql = "SELECT * FROM downloads WHERE status = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.name());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                downloads.add(mapToDownloadDto(resultSet));
            }

        }
        return downloads;
    }

    @Override
    public List<Download> findAll() throws SQLException {
        String sql = "SELECT * FROM downloads";
        List<Download> downloads = new ArrayList<>();

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                downloads.add(mapToDownloadDto(resultSet));
            }
        }
        return downloads;
    }

    @Override
    public void delete(Download entity) throws SQLException {
        String sql = "DELETE FROM downloads WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, entity.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM downloads WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    // Helper method to map ResultSet to DownloadDto
    private Download mapToDownloadDto(ResultSet resultSet) throws SQLException {
        Download download = new Download();
        download.setId(resultSet.getLong("id"));
        download.setUrl(resultSet.getString("url"));
        download.setSize(resultSet.getLong("size"));
        download.setDownloaded(resultSet.getLong("downloaded"));
        download.setStartTime(resultSet.getString("start_time"));
        download.setEndTime(resultSet.getString("end_time"));
        download.setStatus(DownloadStatus.valueOf(resultSet.getString("status")));
        return download;
    }
}

