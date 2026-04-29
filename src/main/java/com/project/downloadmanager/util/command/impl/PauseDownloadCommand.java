package com.project.downloadmanager.util.command.impl;


import com.project.downloadmanager.util.command.Command;
import com.project.downloadmanager.util.template.AbstractDownloadManager;

public class PauseDownloadCommand implements Command {
    private AbstractDownloadManager downloadManager;
    private String url;

    public PauseDownloadCommand(AbstractDownloadManager downloadManager, String url) {
        this.downloadManager = downloadManager;
        this.url = url;
    }

    @Override
    public void execute() {
        downloadManager.pause(url);
        System.out.println("Download paused");
    }

    @Override
    public void undo() {
        downloadManager.resume(url);
    }
}
