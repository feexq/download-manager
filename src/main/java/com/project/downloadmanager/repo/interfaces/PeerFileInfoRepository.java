package com.project.downloadmanager.repo.interfaces;

import com.project.downloadmanager.model.entity.PeerFileInfo;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public interface PeerFileInfoRepository {
    void save(PeerFileInfo peerFileInfo) throws SQLException;
    List<PeerFileInfo> findAll() throws SQLException;
}
