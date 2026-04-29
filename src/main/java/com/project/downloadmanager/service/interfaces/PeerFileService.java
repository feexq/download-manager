package com.project.downloadmanager.service.interfaces;

import com.project.downloadmanager.model.entity.PeerFileInfo;

import java.util.List;

public interface PeerFileService {
    void save(PeerFileInfo peerFileInfo);
    List<PeerFileInfo> getAll();
}
