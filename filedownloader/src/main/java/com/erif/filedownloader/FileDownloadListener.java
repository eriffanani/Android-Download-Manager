package com.erif.filedownloader;

import androidx.annotation.Nullable;

public interface FileDownloadListener {

    public void onDownloadStart(long id);
    public void onDownloadRunning(long id);
    public void onDownloadPaused(long id);
    public void onDownloadStopped(long id);
    public void onDownloadFailed(long id, @Nullable String reason);
    public void onDownloadSuccess(long id, String path);

}
