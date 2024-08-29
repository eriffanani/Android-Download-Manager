package com.erif.filedownloader;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;

public class FileDownloader {

    private static final int CONN_MOBILE = DownloadManager.Request.NETWORK_MOBILE;
    private static final int CONN_WIFI = DownloadManager.Request.NETWORK_WIFI;
    private static final int NOTIFICATION = DownloadManager.Request.VISIBILITY_VISIBLE;
    private static final int NOTIFICATION_HIDE = DownloadManager.Request.VISIBILITY_HIDDEN;

    private final FileDownloadManager manager;
    public FileDownloader(FileDownloadManager manager) {
        this.manager = manager;
    }

    private String title;
    private String description;
    private String url;
    private String saveTo;

    public FileDownloader setTitle(String title) {
        this.title = title;
        return this;
    }

    public FileDownloader setDescription(String description) {
        this.description = description;
        return this;
    }

    public FileDownloader setUrl(@NonNull String url) {
        this.url = url;
        return this;
    }

    public FileDownloader saveTo(String saveTo) {
        this.saveTo = saveTo;
        return this;
    }

    public void download() {
        String[] paths = url.split("/");
        String fileName = paths[paths.length - 1];
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(title == null ? "Download" : title);
        request.setDescription(description == null ? "Downloading "+fileName : description);
        String downloadDir = saveTo == null ? Environment.DIRECTORY_DOWNLOADS : saveTo;
        request.setDestinationInExternalPublicDir(downloadDir, fileName);
        request.setAllowedNetworkTypes(manager.wifiOnly() ? CONN_WIFI : CONN_MOBILE | CONN_WIFI);
        request.setNotificationVisibility(manager.showNotification() ? NOTIFICATION : NOTIFICATION_HIDE);

        long id = manager.getManager().enqueue(request);
        manager.getListener().onDownloadStart(id);
        manager.runTracker();
    }

    public static int percent(int downloadedSize, int fileSize) {
        return (int) ((downloadedSize * 100L) / fileSize);
    }

    public static void sizeFormat(int fileSize, @NonNull CallbackSizeFormat callback) {
        sizeFormat(fileSize, "#0.00", callback);
    }

    public static void sizeFormat(
            int fileSize, @NonNull String decimalPattern,
            @NonNull CallbackSizeFormat callback
    ) {
        DecimalFormat formatter = new DecimalFormat(decimalPattern);
        float sizeInKB = fileSize / 1024f;
        float sizeInMB = sizeInKB / 1024f;
        float sizeInGB = sizeInMB / 1024f;
        float sizeInTB = sizeInGB / 1024f;
        String unit;
        float size;
        if (sizeInTB >= 1) {
            unit = "tb";
            size = sizeInTB;
        } else if (sizeInGB >= 1) {
            unit = "gb";
            size = sizeInGB;
        } else if (sizeInMB >= 1) {
            unit = "mb";
            size = sizeInMB;
        } else if (sizeInKB >= 1){
            unit = "kb";
            size = sizeInKB;
        } else {
            unit = "bytes";
            size = (float) fileSize;
        }
        callback.onFormat(unit, formatter.format(size));
    }

}
