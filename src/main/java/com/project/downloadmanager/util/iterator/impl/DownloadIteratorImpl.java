package com.project.downloadmanager.util.iterator.impl;


import com.project.downloadmanager.model.DownloadDto;
import com.project.downloadmanager.util.iterator.Iterator;

import java.util.List;
import java.util.NoSuchElementException;

public class DownloadIteratorImpl implements Iterator<DownloadDto> {

    private int currentPosition = 0;
    private final List<DownloadDto> downloads;

    public DownloadIteratorImpl(List<DownloadDto> downloads) {
        this.downloads = downloads;
    }

    @Override
    public boolean hasNext() {
        return currentPosition < downloads.size();
    }

    @Override
    public DownloadDto next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return downloads.get(currentPosition++);
    }
}
