package com.erif.filedownloader;

import android.database.Cursor;

public class DownloadItem {

    private final Cursor cursor;
    private final CursorManager manager;

    DownloadItem(Cursor cursor, CursorManager manager) {
        this.cursor = cursor;
        this.manager = manager;
    }

    public String getTitle() {
        return manager.getTitle(cursor);
    }

    public String getDescription() {
        return manager.getDescription(cursor);
    }

    public int getStatus() {
        return manager.getStatus(cursor);
    }

    public String getUrl() {
        return manager.getUrl(cursor);
    }

    public String getUri() {
        return manager.getUri(cursor);
    }

    public int getDownloadedSize() {
        return manager.getDownloadedSize(cursor);
    }

    public int getFileSize() {
        return manager.getTotalSize(cursor);
    }

    public String getReason() {
        return manager.getReason(cursor);
    }

    public void close() {
        cursor.close();
    }

}
