package com.project.downloadmanager.model.entity;

import com.project.downloadmanager.util.composite.DownloadGroup;
import lombok.*;

import java.io.Serializable;
import java.util.Random;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class PeerFileInfo implements Serializable, DownloadGroup {
    Random random = new Random();

    private long id = Math.abs(random.nextLong());
    private String fileName;
    private String address;
    private int port;

    public PeerFileInfo(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public PeerFileInfo(String fileName, String address, int port) {
        this.fileName = fileName;
        this.address = address;
        this.port = port;
    }

    public PeerFileInfo(long id,String fileName, String address, int port) {
        this.id = id;
        this.fileName = fileName;
        this.address = address;
        this.port = port;
    }

    @Override
    public void display() {

    }
}
