package com.project.downloadmanager.model.entity;

import com.project.downloadmanager.model.enums.DownloadStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class Download {
    private long id;
    private String url;
    private long size;
    private long downloaded;
    private String startTime;
    private String endTime;
    private DownloadStatus status;

    public Download(long id, String url, long size, long downloaded, String startTime, String endTime, String status) {
        this.id = id;
        this.url = url;
        this.size = size;
        this.downloaded = downloaded;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = DownloadStatus.valueOf(status);
    }

    @Override
    public String toString() {
        return "Download [url=" + url + ", size=" + size + ", downloaded=" + downloaded + ", startTime=" + startTime + ", endTime=" + endTime + ", status=" + status + "]";
    }
}
