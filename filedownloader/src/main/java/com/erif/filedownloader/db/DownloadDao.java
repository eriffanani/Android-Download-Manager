package com.erif.filedownloader.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface DownloadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DownloadModel model);

    @Query("DELETE FROM tb_download")
    void clear();

    @Query("DELETE FROM tb_download WHERE id=:id")
    void delete(long id);

    @Query("SELECT * FROM tb_download ORDER BY id ASC")
    LiveData<List<DownloadModel>> getAll();

    @Query("SELECT * FROM tb_download WHERE id=:id")
    LiveData<DownloadModel> get(long id);
}
