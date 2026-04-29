package com.project.downloadmanager.model.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DownloadStatistic {
    private long id;
    private int downloads;
    private long downloadsSize;
    private long downloadTotalTime;
}
