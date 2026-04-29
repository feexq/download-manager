package com.project.downloadmanager.service.mapper;

import com.project.downloadmanager.model.DownloadDto;
import com.project.downloadmanager.model.entity.Download;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DownloadMapper {
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public Download toDownload(DownloadDto dto) {
        Download download = new Download();
        download.setId(dto.getId());
        download.setUrl(dto.getUrl());
        download.setDownloaded(dto.getDownloaded());
        download.setStatus(dto.getStatus());
        download.setSize(dto.getSize());
        download.setStartTime(formatDate(dto.getStartTime()));
        download.setEndTime(formatDate(dto.getEndTime()));
        return download;
    }

    private String formatDate(Date date) {
        return date != null ? formatter.format(date) : null;
    }

    public DownloadDto toDownloadDto(Download download) {
        DownloadDto dto = new DownloadDto();
        dto.setId(download.getId());
        dto.setUrl(download.getUrl());
        dto.setDownloaded(download.getDownloaded());
        dto.setStatus(download.getStatus());
        dto.setSize(download.getSize());
        dto.setStartTime(formatDateFromString(download.getStartTime()));
        dto.setEndTime(formatDateFromString(download.getEndTime()));
        return dto;
    }

    private Date formatDateFromString(String dateString) {
        try {
            return formatter.parse(dateString);
        } catch (Exception e) {
            return null;
        }
    }

}
