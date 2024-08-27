package com.erif.filedownloader.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tb_download")
public class DownloadModel {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "destination")
    private String destination;

    public DownloadModel(
            long id, String url,
            String destination
    ) {
        this.id = id;
        this.url = url;
        this.destination = destination;
    }

    public long getId(){return this.id;}
    public String getUrl(){return this.url;}
    public String getDestination(){return this.destination;}

}
