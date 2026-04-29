package com.project.downloadmanager.service;

import com.project.downloadmanager.model.entity.Download;
import com.project.downloadmanager.model.entity.PeerFileInfo;
import com.project.downloadmanager.model.enums.DownloadStatus;
import com.project.downloadmanager.repo.DownloadRepositoryImpl;
import com.project.downloadmanager.model.entity.DownloadStatistic;
import com.project.downloadmanager.repo.DownloadStatisticRepositoryImpl;
import com.project.downloadmanager.repo.interfaces.DownloadRepository;
import com.project.downloadmanager.repo.interfaces.DownloadStatisticRepository;
import com.project.downloadmanager.service.interfaces.PeerFileService;
import com.project.downloadmanager.service.mapper.DownloadMapper;
import com.project.downloadmanager.util.composite.DownloadCategory;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@Setter
public class DownloadManagerStatistic {

    @Getter
    private static DownloadManagerStatistic instance;

    public DownloadManagerStatistic() {
        instance = this;
    }

    private final DownloadRepository downloadRepository = new DownloadRepositoryImpl();
    private final DownloadStatisticRepository statisticRepository = new DownloadStatisticRepositoryImpl();
    private final PeerFileService peerFileService = new PeerFileServiceImpl();
    private final DownloadMapper downloadMapper = new DownloadMapper();

    DownloadCategory activeDownload = new DownloadCategory("Active Download");
    DownloadCategory completeDownloads = new DownloadCategory("Complete Downloads");
    DownloadCategory pausedDownloads = new DownloadCategory("Paused Downloads");
    DownloadCategory failedDownloads = new DownloadCategory("Error Downloads");
    DownloadCategory peerDownloads = new DownloadCategory("Peer downloads");

    public void calculateStatistics() throws SQLException{
        List<Download> downloadList = downloadRepository.findAll();

        activeDownload.addAll(getDownloadCategory().getActiveDownloadGroups(downloadList));
        completeDownloads.addAll(getDownloadCategory().getCompletedDownloadGroups(downloadList));
        pausedDownloads.addAll(getDownloadCategory().getPausedDownloadGroups(downloadList));
        failedDownloads.addAll(getDownloadCategory().getErrorDownloadGroups(downloadList));
        peerDownloads.addAll(getDownloadCategory().getPeerDownloadGroups(peerFileService.getAll()));

        DownloadStatistic statistic = new DownloadStatistic();
        statistic.setDownloads(downloadCount(downloadList));
        statistic.setDownloadsSize(downloadSizeCount(downloadList));
        statistic.setDownloadTotalTime(downloadDaysDuration(downloadList));
        statisticRepository.save(statistic);
    }

    public DownloadStatistic getStatistic() throws SQLException{
        return statisticRepository.findById(1L).orElseThrow();
    }

    public int downloadCount(List<Download> downloadList) {
        return downloadList.size();
    }

    public long downloadSizeCount(List<Download> downloadList) {
        long size = 0;
        for (Download download : downloadList) {
            size += download.getSize();
        }
        return size;
    }

    public long downloadDaysDuration(List<Download> downloadList) {
        if (downloadList.isEmpty()) {
            return 0;
        }
        LocalDate firstDate = downloadList.stream()
                .filter(download -> download.getStartTime() != null)
                .map(download -> LocalDate.parse(download.getStartTime()))
                .min(LocalDate::compareTo)
                .orElseThrow(() -> new RuntimeException("Не знайдено валідної стартової дати"));

        LocalDate lastDate = downloadList.stream()
                .filter(download -> download.getEndTime() != null)
                .map(download -> LocalDate.parse(download.getEndTime()))
                .max(LocalDate::compareTo)
                .orElseThrow(() -> new RuntimeException("Не знайдено валідної кінцевої дати"));

        return ChronoUnit.DAYS.between(firstDate, lastDate);
    }

    public DownloadCategory getDownloadCategory() {
        return new DownloadCategory();
    }
}
