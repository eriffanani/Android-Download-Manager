package com.erif.filedownloader;

public interface FileDownloadListener {

    public void onDownloadStart(long id, String url);
    public void onDownloadRunning(long id, String url, long downloadedSize, long fileSize);
    public void onDownloadPaused(long id, String url);
    public void onDownloadStopped(long id, String url);
    public void onDownloadFailed(long id, String url);
    public void onDownloadSuccess(long id, String url, String path);

}
