package com.erif.filedownloader;

import android.app.DownloadManager;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileDownloadChecker {

    private final FileDownloadManager manager;
    private Handler handler;
    private static final long INTERVAL = 350L;
    private boolean canInterrupt = false;
    private final Map<Long, Integer> tmp = new HashMap<>();

    public FileDownloadChecker(FileDownloadManager manager) {
        this.manager = manager;
    }

    void runChecker(boolean canInterrupt) {
        this.canInterrupt = canInterrupt;
        if (handler == null)
            handler = new Handler();
        handler.postDelayed(runnable, INTERVAL);
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                checkActiveDownload();
            } finally {
                if (handler != null)
                    handler.postDelayed(runnable, INTERVAL);
            }
        }
    };

    private void checkActiveDownload() {
        log("Checker running");
        Cursor cursor = manager.getManager().query(manager.queryRunningAll());
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long id = manager.getDownloadId(cursor);
                int bytesDownloaded = cursor.getInt(getBytesDownloaded(cursor));
                if (!tmp.containsKey(id))
                    tmp.put(id, bytesDownloaded);
            }
        } else {
            if (canInterrupt) {
                stopChecker();
            }
        }
        List<Long> idForDelete = new ArrayList<>();
        tmp.forEach((id, downloaded) -> {
            DownloadManager.Query query = new DownloadManager.Query().setFilterById(id);
            Cursor cursorListed = manager.getManager().query(query);
            if (cursorListed.moveToFirst()) {
                int idxURI = cursorListed.getColumnIndex(DownloadManager.COLUMN_URI);
                String url = cursorListed.getString(idxURI);
                int bytesDownloaded = cursorListed.getInt(getBytesDownloaded(cursorListed));
                int bytesTotal = cursorListed.getInt(getBytesTotal(cursorListed));
                if (bytesTotal > 0) {
                    int idxStatus = cursorListed.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cursorListed.getInt(idxStatus);
                    manager.getListener().onDownloadRunning(
                            id, url, kb(bytesDownloaded), kb(bytesTotal)
                    );
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        idForDelete.add(id);
                    }
                }
            }
        });
        idForDelete.forEach(tmp::remove);
        if (tmp.isEmpty()) {
            stopChecker();
        }
        cursor.close();
    }

    private long kb(long value) {
        return value / 1024;
    }

    void stopChecker() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler.removeCallbacks(runnable);
            handler = null;
        }
        log("Checker stopped");
    }

    private int getBytesDownloaded(Cursor cursor) {
        return cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
    }

    private int getBytesTotal(Cursor cursor) {
        return cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
    }

    private void log(String msg) {
        Log.e("Downloader Lib", msg);
    }

}
