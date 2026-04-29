package com.project.downloadmanager.repo;

import com.project.downloadmanager.model.entity.PeerFileInfo;
import com.project.downloadmanager.repo.interfaces.PeerFileInfoRepository;
import com.project.downloadmanager.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeerFileInfoRepositoryImpl implements PeerFileInfoRepository {


    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    @Override
    public void save(PeerFileInfo peerFileInfo) throws SQLException {
        String query = "INSERT INTO peer_file_info (id, fileName, address, port) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setLong(1, peerFileInfo.getId());
            statement.setString(2, peerFileInfo.getFileName());
            statement.setString(3, peerFileInfo.getAddress());
            statement.setInt(4, peerFileInfo.getPort());
            statement.executeUpdate();
        }
    }

    @Override
    public List<PeerFileInfo> findAll() throws SQLException {
        List<PeerFileInfo> peerFiles = new ArrayList<>();
        String query = "SELECT * FROM peer_file_info";
        try (Statement statement = getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                PeerFileInfo peerFileInfo = new PeerFileInfo(
                        resultSet.getLong("id"),
                        resultSet.getString("fileName"),
                        resultSet.getString("address"),
                        resultSet.getInt("port")
                );
                peerFiles.add(peerFileInfo);
            }
        }
        return peerFiles;
    }
}
