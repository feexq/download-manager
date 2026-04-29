package com.project.downloadmanager.util.command.impl;


import com.project.downloadmanager.util.command.Command;
import com.project.downloadmanager.util.template.AbstractDownloadManager;

public class ResumeDownloadCommand implements Command {

    private AbstractDownloadManager downloadManager;
    private String url;

    public ResumeDownloadCommand(AbstractDownloadManager downloadManager, String url) {
        this.url = url;
        this.downloadManager = downloadManager;
    }

    @Override
    public void execute() {
        downloadManager.resume(url);
        System.out.println("Download resumed");
    }

    @Override
    public void undo() {
        downloadManager.pause(url);
    }
}
