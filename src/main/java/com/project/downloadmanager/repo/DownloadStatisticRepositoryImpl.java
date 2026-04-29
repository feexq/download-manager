package com.project.downloadmanager.repo;

import com.project.downloadmanager.model.entity.DownloadStatistic;
import com.project.downloadmanager.repo.interfaces.DownloadStatisticRepository;
import com.project.downloadmanager.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DownloadStatisticRepositoryImpl implements DownloadStatisticRepository {

    private static final long SINGLE_RECORD_ID = 1L;

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    @Override
    public Optional<DownloadStatistic> findById(Long id) throws SQLException {
        if (id != SINGLE_RECORD_ID) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM download_statistics WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, SINGLE_RECORD_ID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(createStatisticFromResultSet(rs));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public List<DownloadStatistic> findAll() throws SQLException {
        List<DownloadStatistic> statistics = new ArrayList<>();
        String sql = "SELECT * FROM download_statistics WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, SINGLE_RECORD_ID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    statistics.add(createStatisticFromResultSet(rs));
                }
            }
        }

        return statistics;
    }

    @Override
    public DownloadStatistic save(DownloadStatistic statistic) throws SQLException {
        String insertSql = """
            INSERT INTO download_statistics (id, downloads, downloads_size, download_total_time)
            VALUES (?, ?, ?, ?)
        """;
        String updateSql = """
            UPDATE download_statistics
            SET downloads = ?, downloads_size = ?, download_total_time = ?
            WHERE id = ?
        """;

        try (Connection conn = getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setLong(1, SINGLE_RECORD_ID);
                pstmt.setInt(2, statistic.getDownloads());
                pstmt.setLong(3, statistic.getDownloadsSize());
                pstmt.setLong(4, statistic.getDownloadTotalTime());
                setStatementParameters(pstmt, statistic);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setInt(1, statistic.getDownloads());
                    pstmt.setLong(2, statistic.getDownloadsSize());
                    pstmt.setLong(3, statistic.getDownloadTotalTime());
                    pstmt.setLong(4, SINGLE_RECORD_ID);
                    pstmt.executeUpdate();
                }
            }
        }

        return statistic;
    }

    private void setStatementParameters(PreparedStatement pstmt, DownloadStatistic statistic) throws SQLException {
        pstmt.setInt(2, statistic.getDownloads());
        pstmt.setLong(3, statistic.getDownloadsSize());
        pstmt.setLong(4, statistic.getDownloadTotalTime());
    }

    @Override
    public void deleteById(Long id) throws SQLException {
        if (id != SINGLE_RECORD_ID) {
            return; // Якщо намагаються видалити не той запис, просто повертаємося
        }

        String sql = "DELETE FROM download_statistics WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, SINGLE_RECORD_ID);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(DownloadStatistic downloadStatistic) throws SQLException {
        deleteById(downloadStatistic.getId());
    }

    private DownloadStatistic createStatisticFromResultSet(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        int downloads = rs.getInt("downloads");
        long downloadsSize = rs.getLong("downloads_size");
        long downloadTotalTime = rs.getLong("download_total_time");

        return new DownloadStatistic(id, downloads, downloadsSize, downloadTotalTime);
    }
}
