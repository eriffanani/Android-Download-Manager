package com.erif.filedownloader.db;

import android.content.Context;

import androidx.lifecycle.LiveData;
import java.util.List;

public class DownloadRepository {

    private final DownloadDao dao;
    private final LiveData<List<DownloadModel>> allData;

    public DownloadRepository(Context context) {
        DownloadDb db = DownloadDb.getDatabase(context);
        dao = db.dao();
        allData = dao.getAll();
    }

    public void insert(DownloadModel model) {
        DownloadDb.databaseWriteExecutor.execute(() -> dao.insert(model));
    }

    public void clear() {
        DownloadDb.databaseWriteExecutor.execute(dao::clear);
    }

    public void delete(long id) {
        DownloadDb.databaseWriteExecutor.execute(() -> dao.delete(id));
    }

    public LiveData<List<DownloadModel>> getAll() {return allData;}

    public LiveData<DownloadModel> get(long id) {return dao.get(id);}

}
