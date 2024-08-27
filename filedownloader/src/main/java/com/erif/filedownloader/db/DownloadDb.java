package com.erif.filedownloader.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {DownloadModel.class}, version = 1, exportSchema = false)
public abstract class DownloadDb extends RoomDatabase {

    public abstract DownloadDao dao();

    private static volatile DownloadDb INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static DownloadDb getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DownloadDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(), DownloadDb.class,
                            "downloads_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }

}
