package com.erif.filedownloader;

import android.app.DownloadManager;

public class QueryManager {

    DownloadManager.Query query(int status) {
        return new DownloadManager.Query()
                .setFilterByStatus(status);
    }

    DownloadManager.Query query(long id) {
        return new DownloadManager.Query()
                .setFilterById(id);
    }

    DownloadManager.Query running() {
        return query(DownloadManager.STATUS_RUNNING);
    }

    DownloadManager.Query runningAll() {
        return new DownloadManager.Query()
                .setFilterByStatus(
                        DownloadManager.STATUS_RUNNING | DownloadManager.STATUS_PENDING
                );
    }

    DownloadManager.Query downloaded() {
        return query(DownloadManager.STATUS_SUCCESSFUL);
    }

    DownloadManager.Query failed() {
        return query(DownloadManager.STATUS_FAILED);
    }

    DownloadManager.Query paused() {
        return query(DownloadManager.STATUS_PAUSED);
    }

}
