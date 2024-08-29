package com.erif.filedownloaderdemo.example.multiple

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.webkit.MimeTypeMap
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.erif.filedownloader.FileDownloadListener
import com.erif.filedownloader.FileDownloadManager
import com.erif.filedownloader.FileDownloader
import com.erif.filedownloaderdemo.R
import com.google.android.material.button.MaterialButton
import java.io.File

class ActMultipleFileDownload : AppCompatActivity(), OnClickItemListener, FileDownloadListener {

    private lateinit var manager: FileDownloadManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterMultipleDownload

    private val urlsLarge = arrayOf(
        "https://link.testfile.org/iK7sKT",
        "https://media.neliti.com/media/publications/249244-none-837c3dfb.pdf",
        "https://jsoncompare.org/LearningContainer/SampleFiles/Video/MP4/Sample-MP4-Video-File-Download.mp4",
        "https://link.testfile.org/aYr11v"
    )

    private val urlsSmall = arrayOf(
        "https://link.testfile.org/aXCg7h",
        "https://media.neliti.com/media/publications/249244-none-837c3dfb.pdf",
        "https://link.testfile.org/DNnCeI",
        "https://link.testfile.org/bNYZFw"
    )

    private val urls = urlsSmall

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.act_multiple_file_download)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setSupportActionBar(findViewById(R.id.toolbar))
        val btnDownloadAll: MaterialButton = findViewById(R.id.buttonMultipleDownload)
        manager = FileDownloadManager(this, this)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.layoutAnimation = AnimationUtils.loadLayoutAnimation(
            this, R.anim.layout_anim_fade_in
        )
        clearRecyclerAnim(recyclerView)
        recyclerView.addItemDecoration(
            DividerItemDecoration(this, LinearLayout.VERTICAL)
        )

        val list: MutableList<ModelItemMultiple> = ArrayList()
        urls.forEach { url ->
            val paths = url.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val fileName = paths[paths.size - 1]
            val item = ModelItemMultiple(
                1, url, fileName, 0, null, null
            )
            list.add(item)
        }
        adapter = AdapterMultipleDownload(list, this)
        Handler(Looper.getMainLooper()).postDelayed({
            recyclerView.adapter = adapter
            recyclerView.scheduleLayoutAnimation()
            manager.trackDownloads()
        }, 700)

        btnDownloadAll.setOnClickListener {
            adapter.getList().forEach { item ->
                FileDownloader(manager)
                    .setUrl(item.url)
                    .download()
            }
        }

    }

    override fun onClickItem(item: ModelItemMultiple) {
        if (item.status == DownloadStatus.DOWNLOADED) {
            item.path?.let { openFile(File(it)) }
        } else {
            FileDownloader(manager)
                .setUrl(item.url)
                .download()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun clearRecyclerAnim(recyclerView: RecyclerView) {
        (recyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        (recyclerView.itemAnimator as DefaultItemAnimator).endAnimations()
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        (recyclerView.itemAnimator)?.changeDuration = 0
    }

    override fun onDownloadStart(id: Long) {
        manager.findDownloadById(id)?.apply {
            val position = adapter.findPosition(url)
            close()
            adapter.downloadStart(position)
        }
    }

    override fun onDownloadRunning(id: Long) {
        manager.findDownloadById(id)?.apply {
            val percent = FileDownloader.percent(downloadedSize, fileSize)
            var progressSize = "0bytes"
            FileDownloader.sizeFormat(downloadedSize) { unit, size ->
                progressSize = "$size${unit.uppercase()}"
            }

            var totalSize = "0bytes"
            FileDownloader.sizeFormat(fileSize) { unit, size ->
                totalSize = "$size${unit.uppercase()}"
            }
            val position = adapter.findPosition(url)
            adapter.updateProgress(position, percent, progressSize, totalSize)
        }
    }

    override fun onDownloadPaused(id: Long) {
        TODO("Not yet implemented")
    }

    override fun onDownloadStopped(id: Long) {
        manager.findDownloadById(id)?.apply {
            val position = adapter.findPosition(url)
            adapter.reset(position)
        }
    }

    override fun onDownloadFailed(id: Long, reason: String?) {

    }

    override fun onDownloadSuccess(id: Long, path: String) {
        manager.findDownloadById(id)?.apply {
            var totalSize = "0bytes"
            FileDownloader.sizeFormat(fileSize) { unit, size ->
                totalSize = "$size${unit.uppercase()}"
                log("Download success $id: (Size: $totalSize) $path")
            }
            val position = adapter.findPosition(url)
            close()
            adapter.downloadSuccess(position, totalSize, path)
        }
    }

    override fun onPause() {
        super.onPause()
        manager.onPause()
    }

    override fun onResume() {
        super.onResume()
        manager.onResume()
    }

    private fun log(msg: String) = Log.e("Download Multiple", msg)

    private fun openFile(file: File) {
        if (file.exists()) {
            val uri = FileProvider.getUriForFile(
                this, "${packageName}.provider", file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                clipData = ClipData.newRawUri("", uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            val getExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            val mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(getExtension)
            val undefinedExtension =
                getExtension.equals("", ignoreCase = true) || mimetype == null
            val extension = if (undefinedExtension) "text/*" else mimetype
            intent.setDataAndType(uri, extension)
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                //Toast.makeText(this, "An error occurs when opening the file.", Toast.LENGTH_SHORT).show()
                log("Open document failed: ${e.message}")
            }
        } else {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show()
        }
    }

}