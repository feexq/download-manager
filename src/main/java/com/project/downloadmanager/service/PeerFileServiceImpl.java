package com.project.downloadmanager.service;

import com.project.downloadmanager.model.entity.PeerFileInfo;
import com.project.downloadmanager.repo.PeerFileInfoRepositoryImpl;
import com.project.downloadmanager.repo.interfaces.PeerFileInfoRepository;
import com.project.downloadmanager.service.interfaces.PeerFileService;

import java.util.List;

public class PeerFileServiceImpl implements PeerFileService {

    private PeerFileInfoRepository peerFileInfoRepository = new PeerFileInfoRepositoryImpl();

    @Override
    public void save(PeerFileInfo peerFileInfo) {
        try {
            peerFileInfoRepository.save(peerFileInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<PeerFileInfo> getAll() {
        try {
            return peerFileInfoRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
