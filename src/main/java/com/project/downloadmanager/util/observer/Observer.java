package com.project.downloadmanager.util.observer;


import com.project.downloadmanager.model.DownloadDto;

public interface Observer {

    void update(DownloadDto dto);

}
