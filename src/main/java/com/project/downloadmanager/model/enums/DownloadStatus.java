package com.project.downloadmanager.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public enum DownloadStatus {
    PENDING("PENDING"),
    DOWNLOADING("DOWNLOADING"),
    PAUSED("PAUSED"),
    ERROR("ERROR"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED");

    private final String name;

    public static DownloadStatus fromName(String name) {
        for (DownloadStatus status : values()) {
            if (status.name.equalsIgnoreCase(name)) {
                return status;
            }
        }
        throw new IllegalArgumentException(String.format("Category type '%s' not found", name));
    }
}
