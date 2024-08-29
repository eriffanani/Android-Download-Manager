package com.erif.filedownloader;

import android.app.DownloadManager;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadTracker {

    private static final String TAG = "File Download Libs";

    private final DownloadManager manager;
    private final QueryManager query;
    private final CursorManager cursor;
    private final FileDownloadListener listener;

    private Handler handler;
    private static final long INTERVAL = 300L;
    private boolean canInterrupt = false;
    private final Map<Long, Integer> tmp = new HashMap<>();
    private boolean isTracking = false;

    public DownloadTracker(FileDownloadManager downloadManager) {
        manager = downloadManager.getManager();
        query = downloadManager.getQuery();
        cursor = downloadManager.getCursor();
        listener = downloadManager.getListener();
    }

    void run(boolean canInterrupt) {
        this.canInterrupt = canInterrupt;
        if (!isTracking) {
            if (handler == null)
                handler = new Handler();
            handler.postDelayed(runnable, INTERVAL);
            isTracking = true;
        }
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
        Log.d(TAG, "Tracker running");
        Cursor mCursor = manager.query(query.runningAll());
        if (mCursor.getCount() > 0) {
            while (mCursor.moveToNext()) {
                long id = cursor.getId(mCursor);
                int bytesDownloaded = cursor.getDownloadedSize(mCursor);
                if (!tmp.containsKey(id))
                    tmp.put(id, bytesDownloaded);
            }
        } else {
            if (canInterrupt)
                stopTracker();
        }
        List<Long> idForDelete = new ArrayList<>();
        tmp.forEach((id, downloaded) -> {
            Cursor cursorList = manager.query(query.query(id));
            if (cursorList.moveToFirst()) {
                int bytesTotal = cursor.getTotalSize(cursorList);
                if (bytesTotal > 0) {
                    int status = cursor.getStatus(cursorList);
                    listener.onDownloadRunning(id);
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        idForDelete.add(id);
                    }
                }
            }
            cursorList.close();
        });
        idForDelete.forEach(tmp::remove);
        if (tmp.isEmpty())
            stopTracker();
        mCursor.close();
    }

    void stopTracker() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler.removeCallbacks(runnable);
            handler = null;
        }
        isTracking = false;
        Log.e(TAG, "Tracker stopped");
    }

}
