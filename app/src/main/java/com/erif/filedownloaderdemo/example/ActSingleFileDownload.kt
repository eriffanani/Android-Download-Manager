package com.erif.filedownloaderdemo.example

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.erif.filedownloader.FileDownloadListener
import com.erif.filedownloader.FileDownloadManager
import com.erif.filedownloader.FileDownloader
import com.erif.filedownloaderdemo.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.text.DecimalFormat

class ActSingleFileDownload : AppCompatActivity(), FileDownloadListener {

    private var downloadManager: FileDownloadManager? = null
    private lateinit var txtPercent: TextView
    private lateinit var txtProgressSize: TextView
    private lateinit var txtTotalSize: TextView
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var button: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.act_single_file_download)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        txtPercent = findViewById(R.id.txtPercent)
        txtProgressSize = findViewById(R.id.txtProgressSize)
        txtTotalSize = findViewById(R.id.txtTotalSize)
        progressBar = findViewById(R.id.progress)
        button = findViewById(R.id.button)
        downloadManager = FileDownloadManager(this, this, this)

        button.setOnClickListener {
            val url = "https://jsoncompare.org/LearningContainer/SampleFiles/Video/MP4/Sample-MP4-Video-File-Download.mp4"
            //val url = "https://media.neliti.com/media/publications/249244-none-837c3dfb.pdf"
            FileDownloader(downloadManager)
                .setUrl(url)
                .download()
        }

    }

    override fun onPause() {
        super.onPause()
        downloadManager?.onPause()
    }

    override fun onResume() {
        super.onResume()
        downloadManager?.onResume()
    }

    override fun onDownloadStart(id: Long, url: String) {
        progressBar.isIndeterminate = true
    }

    override fun onDownloadRunning(
        id: Long, url: String,
        downloadedSize: Long, fileSize: Long
    ) {
        log("id: $id - Url: $url")
        progressBar.isIndeterminate = false
        val percent = ((downloadedSize * 100L) / fileSize).toInt()
        progressBar.setProgress(percent, true)
        val percentage = "$percent%"
        txtPercent.text = percentage

        val formatter = DecimalFormat("#0.00")
        FileDownloadManager.sizeFormat(downloadedSize).entries.forEach { map ->
            val sizeName = map.key.uppercase()
            val sizeValue = formatter.format(map.value)
            val totalSize = "$sizeValue$sizeName"
            txtProgressSize.text = totalSize
        }

        val totalFormat = FileDownloadManager.sizeFormat(fileSize)
        totalFormat.entries.forEach { map ->
            val sizeName = map.key.uppercase()
            val sizeValue = formatter.format(map.value)
            val totalSize = "$sizeValue$sizeName"
            txtTotalSize.text = totalSize
        }
    }

    override fun onDownloadPaused(id: Long, url: String) {
        toast("Download File $id Paused")
    }

    override fun onDownloadStopped(id: Long, url: String) {
        toast("Download File $id Stopped")
    }

    override fun onDownloadFailed(id: Long, url: String) {
        toast("Download File $id Failed")
    }

    override fun onDownloadSuccess(id: Long, url: String, path: String?) {
        toast("Download File $id Success: $path")
        log("Download Success $id: $path")
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun log(message: String) {
        Log.e("Download Library", message)
    }

}