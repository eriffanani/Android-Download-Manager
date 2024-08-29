package com.erif.filedownloader;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.File;

public class FileDownloadManager {

    // Constant
    private static final String DOWNLOAD_COMPLETE = DownloadManager.ACTION_DOWNLOAD_COMPLETE;

    // Download Manager
    private final DownloadManager manager;
    DownloadManager getManager() {return manager;}

    // Listener
    private final FileDownloadListener listener;
    FileDownloadListener getListener(){return listener;}

    // Query Manager
    private final QueryManager query;
    QueryManager getQuery(){return query;}

    // Cursor Manager
    private final CursorManager cursor;
    CursorManager getCursor(){return cursor;}

    // Download Tracker
    private final DownloadTracker tracker;

    // Properties
    private boolean wifiOnly = false;
    private boolean showNotification = false;

    // Constructor
    public FileDownloadManager(
            @NonNull Context context,
            @NonNull FileDownloadListener listener
    ) {
        this.listener = listener;
        this.manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        query = new QueryManager();
        cursor = new CursorManager();
        tracker = new DownloadTracker(this);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String action = intent.getAction();
                    boolean isDownloadComplete = action != null && action.equals(DOWNLOAD_COMPLETE);
                    if (isDownloadComplete) {
                        if (context != null) {
                            Cursor mCursor = manager.query(query.downloaded());
                            if (mCursor.moveToFirst()) {
                                long id = cursor.getId(mCursor);
                                String uri = cursor.getUri(mCursor);
                                File file = uriToFile(uri);
                                if (file.exists())
                                    listener.onDownloadSuccess(id, file.getAbsolutePath());
                                else
                                    listener.onDownloadStopped(id);
                            }
                        }
                    }
                }
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
        Cursor mCursor = manager.query(query.runningAll());
        while (mCursor.moveToNext()) {
            long id = cursor.getId(mCursor);
            manager.remove(id);
        }
        mCursor.close();
    }

    public void onPause() {
        tracker.stopTracker();
    }

    public void onResume() {
        tracker.run(true);
    }

    public void trackDownloads() {
        getDownloadedFile();
    }

    void runTracker() {
        tracker.run(false);
    }

    private void getDownloadedFile() {
        Cursor mCursor = manager.query(query.downloaded());
        while (mCursor.moveToNext()) {
            long id = cursor.getId(mCursor);
            String uri = cursor.getUri(mCursor);
            File file = uriToFile(uri);
            if (file.exists())
                listener.onDownloadSuccess(id, file.getAbsolutePath());
            else
                listener.onDownloadStopped(id);
        }
        mCursor.close();
    }

    private File uriToFile(String uri) {
        Uri mUri = Uri.parse(uri);
        return new File(mUri.getPath());
    }

    public @Nullable DownloadItem findDownloadById(long id) {
        Cursor mCursor = manager.query(query.query(id));
        DownloadItem downloadItem = null;
        if (mCursor.moveToFirst())
            downloadItem = new DownloadItem(mCursor, cursor);
        return downloadItem;
    }

}
