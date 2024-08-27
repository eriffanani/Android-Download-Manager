package com.erif.filedownloader;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import java.util.HashMap;
import java.util.Map;

public class FileDownloadManager {

    private static final String DOWNLOAD_COMPLETE = DownloadManager.ACTION_DOWNLOAD_COMPLETE;

    private final Context context;

    private final DownloadManager manager;
    DownloadManager getManager() {return manager;}

    private final FileDownloadListener listener;
    FileDownloadListener getListener(){return listener;}

    private boolean wifiOnly = false;
    private boolean showNotification = false;

    private final FileDownloadChecker checker;

    public FileDownloadManager(
            @NonNull Context context,
            @NonNull LifecycleOwner owner,
            @NonNull FileDownloadListener listener
    ) {
        this.context = context;
        this.listener = listener;
        this.manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //DownloadRepository repository = new DownloadRepository(context);
        checker = new FileDownloadChecker(this);
        //repository.insert(new DownloadModel(1, "https://www.modot.com", "Download"));
        /* Optional
            repository.getAll().observe(owner, downloads -> {
                for (DownloadModel download: downloads) {
                    long id = download.getId();
                    String url = download.getUrl();
                    String dest = download.getDestination();
                    Log.e("Downloader Lib", "Id: "+id+" URL: "+url+" Destination: "+dest);
                }
            });
        */

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    if (isDownloadComplete(intent)) {
                        Log.d("TAG", "onReceive");
                        if (context != null) {
                            Cursor mCursor = manager.query(
                                    new DownloadManager.Query()
                                            .setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL)
                            );
                            if (mCursor.moveToFirst()) {
                                long id = mCursor.getLong(indexId(mCursor));
                                int idxUrl = mCursor.getColumnIndex(DownloadManager.COLUMN_URI);
                                int idxPath = mCursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                                String url = mCursor.getString(idxUrl);
                                String path = mCursor.getString(idxPath);
                                listener.onDownloadSuccess(id, url, path);
                                /* Optional
                                    manager.remove(id);
                                    repository.delete(id);
                                */
                            }
                        }
                    }
                }
            }

            private boolean isDownloadComplete(Intent intent) {
                String action = intent.getAction();
                return action != null && action.equals(DOWNLOAD_COMPLETE);
            }

        };
        IntentFilter intentFilter = new IntentFilter(DOWNLOAD_COMPLETE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                    receiver, intentFilter, Context.RECEIVER_EXPORTED
            );
        } else {
            ContextCompat.registerReceiver(
                    context, receiver, intentFilter, ContextCompat.RECEIVER_EXPORTED
            );
        }

    }

    public void useWifiOnly(boolean wifiOnly) {
        this.wifiOnly = wifiOnly;
    }
    boolean wifiOnly() {return wifiOnly;}

    public void showNotification(boolean show) {
        this.showNotification = show;
    }
    boolean showNotification() {return showNotification;}

    public void stop(long id) {
        manager.remove(id);
    }

    public void stopAll() {
        Cursor cursor = manager.query(queryRunning());
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(DownloadManager.COLUMN_ID);
            long id = cursor.getLong(idx);
            manager.remove(id);
        }
        cursor.close();
    }

    private DownloadManager.Query query(int status) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(status);
        return query;
    }

    private int indexId(Cursor cursor) {
        return cursor.getColumnIndex(DownloadManager.COLUMN_ID);
    }

    long getDownloadId(Cursor cursor) {
        return cursor.getLong(indexId(cursor));
    }

    DownloadManager.Query queryRunning() {
        return new DownloadManager.Query()
                .setFilterByStatus(DownloadManager.STATUS_RUNNING);
    }

    DownloadManager.Query queryRunningAll() {
        return new DownloadManager.Query()
                .setFilterByStatus(
                        DownloadManager.STATUS_RUNNING |
                        DownloadManager.STATUS_PENDING
                );
    }

    private DownloadManager.Query querySuccess() {
        return new DownloadManager.Query()
                .setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
    }

    private DownloadManager.Query queryFailed() {
        return new DownloadManager.Query()
                .setFilterByStatus(DownloadManager.STATUS_FAILED);
    }

    private DownloadManager.Query queryPaused() {
        return new DownloadManager.Query()
                .setFilterByStatus(DownloadManager.STATUS_PAUSED);
    }

    public void onPause() {
        checker.stopChecker();
    }

    public void onResume() {
        checker.runChecker(true);
    }

    void runChecker() {
        checker.runChecker(false);
    }

    public static Map<String, Float> sizeFormat(long fileSizeInKb) {
        Map<String, Float> map = new HashMap<>();
        float totalInTB = fileSizeInKb / 1024f / 1024f / 1024f;
        float totalInGB = fileSizeInKb / 1024f / 1024f;
        float totalInMB = fileSizeInKb / 1024f;
        if (totalInTB >= 1) {
            map.put("tb", totalInTB);
        } else if (totalInGB >= 1) {
            map.put("gb", totalInGB);
        } else if (totalInMB >= 1) {
            map.put("mb", totalInMB);
        } else {
            map.put("kb", (float) fileSizeInKb);
        }
        return map;
    }

}
