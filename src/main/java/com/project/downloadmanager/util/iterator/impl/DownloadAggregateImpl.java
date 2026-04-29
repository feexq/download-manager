package com.project.downloadmanager.util.iterator.impl;


import com.project.downloadmanager.model.DownloadDto;
import com.project.downloadmanager.util.iterator.Aggregate;
import com.project.downloadmanager.util.iterator.Iterator;

import java.util.List;

public class DownloadAggregateImpl implements Aggregate<DownloadDto> {

    private List<DownloadDto> downloads;

    public DownloadAggregateImpl(List<DownloadDto> downloads) {
        this.downloads = downloads;
    }

    @Override
    public void add(DownloadDto download) {
        downloads.add(download);
    }

    @Override
    public void remove(DownloadDto download) {
        downloads.remove(download);
    }

    @Override
    public Iterator<DownloadDto> createIterator() {
        return new DownloadIteratorImpl(downloads);
    }
}
