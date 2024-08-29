package com.erif.filedownloaderdemo.example.multiple

data class ModelItemMultiple (
    val id: Int,
    val url: String,
    val title: String,
    var percent: Int,
    var progressSize: String?,
    var totalSize: String?,
    var path: String? = null,
    var status: Int = DownloadStatus.NOT_DOWNLOADED,
    var downloadId: Long = -1
)