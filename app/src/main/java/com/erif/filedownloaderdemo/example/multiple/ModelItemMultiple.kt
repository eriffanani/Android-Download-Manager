package com.erif.filedownloaderdemo.example.multiple

data class ModelItemMultiple (
    val id: Int,
    val url: String,
    val title: String,
    var progress: Int,
    var totalSize: Int,
    var status: Int = DownloadStatus.NOT_DOWNLOADED,
    var downloadId: Long = -1
)