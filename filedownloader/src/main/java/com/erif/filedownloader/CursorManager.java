package com.erif.filedownloader;

import android.app.DownloadManager;
import android.database.Cursor;

public class CursorManager {

    private static final String ID = DownloadManager.COLUMN_ID;
    private static final String TITLE = DownloadManager.COLUMN_TITLE;
    private static final String DESC = DownloadManager.COLUMN_DESCRIPTION;
    private static final String STATUS = DownloadManager.COLUMN_STATUS;
    private static final String URL = DownloadManager.COLUMN_URI;
    private static final String URI = DownloadManager.COLUMN_LOCAL_URI;
    private static final String DOWNLOADED_SIZE = DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR;
    private static final String TOTAL_SIZE = DownloadManager.COLUMN_TOTAL_SIZE_BYTES;
    private static final String REASON = DownloadManager.COLUMN_REASON;

    private int index(Cursor cursor, String column) {
        return cursor.getColumnIndex(column);
    }

    long getId(Cursor cursor) {
        return cursor.getLong(index(cursor, ID));
    }

    String getTitle(Cursor cursor) {
        return cursor.getString(index(cursor, TITLE));
    }

    String getDescription(Cursor cursor) {
        return cursor.getString(index(cursor, DESC));
    }

    int getStatus(Cursor cursor) {
        return cursor.getInt(index(cursor, STATUS));
    }

    String getUrl(Cursor cursor) {
        return cursor.getString(index(cursor, URL));
    }

    String getUri(Cursor cursor) {
        return cursor.getString(index(cursor, URI));
    }

    int getDownloadedSize(Cursor cursor) {
        return cursor.getInt(index(cursor, DOWNLOADED_SIZE));
    }

    int getTotalSize(Cursor cursor) {
        return cursor.getInt(index(cursor, TOTAL_SIZE));
    }

    String getReason(Cursor cursor) {
        return cursor.getString(index(cursor, REASON));
    }

}
