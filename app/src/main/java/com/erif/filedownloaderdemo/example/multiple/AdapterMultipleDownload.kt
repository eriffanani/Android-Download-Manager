package com.erif.filedownloaderdemo.example.multiple

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.erif.filedownloaderdemo.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator

class AdapterMultipleDownload(
    private val list: MutableList<ModelItemMultiple>,
    private val listener: OnClickItemListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_multiple_download, parent, false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Holder)
            holder.bind(list[position])
    }

    fun downloadStart(position: Int) {
        if (position >= 0) {
            list[position].status = DownloadStatus.DOWNLOAD_START
            notifyItemChanged(position)
        }
    }

    private fun downloading(position: Int) {
        val item = list[position]
        val currentStatus = item.status
        if (currentStatus != DownloadStatus.DOWNLOADING) {
            item.status = DownloadStatus.DOWNLOADING
            notifyItemChanged(position)
        }
    }

    fun updateProgress(
        position: Int, percent: Int,
        progressSize: String?, totalSize: String?
    ) {
        if (position >= 0) {
            val item = list[position]
            if (item.status != DownloadStatus.DOWNLOADED) {
                downloading(position)
                item.totalSize
                item.percent = percent
                item.progressSize = progressSize
                item.totalSize = totalSize
                notifyItemChanged(position)
            }
        }
    }

    fun downloadSuccess(position: Int, totalSize: String?, path: String?) {
        if (position >= 0) {
            val item = list[position]
            if (item.percent < 100) {
                updateProgress(position, 100, item.totalSize, item.totalSize)
                Handler(Looper.getMainLooper()).postDelayed({
                    item.progressSize = item.totalSize
                    item.path = path
                    item.status = DownloadStatus.DOWNLOADED
                    notifyItemChanged(position)
                }, 800L)
            } else {
                item.progressSize = totalSize
                item.totalSize = totalSize
                item.path = path
                item.status = DownloadStatus.DOWNLOADED
                notifyItemChanged(position)
            }
        }
    }

    fun reset(position: Int) {
        if (position >= 0) {
            val item = list[position]
            item.status = DownloadStatus.NOT_DOWNLOADED
            notifyItemChanged(position)
        }
    }

    fun findPosition(url: String?): Int {
        var pos = -1
        list.find { it.url == url }?.let { found ->
            pos = list.indexOf(found)
        }
        return pos
    }

    fun getList(): MutableList<ModelItemMultiple> = list

    fun getItem(position: Int): ModelItemMultiple {
        return list[position]
    }

    inner class Holder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val txtTitle: TextView = itemView.findViewById(R.id.item_txtTitle)
        private val txtSubtitle: TextView = itemView.findViewById(R.id.item_txtSubtitle)
        private val button: MaterialButton = itemView.findViewById(R.id.item_button)
        private val progress: CircularProgressIndicator = itemView.findViewById(R.id.item_progressBar)

        fun bind(item: ModelItemMultiple) {
            txtTitle.text = item.title
            val notDownloaded = item.status == DownloadStatus.NOT_DOWNLOADED
            val downloadStart = item.status == DownloadStatus.DOWNLOAD_START
            val downloading = item.status == DownloadStatus.DOWNLOADING
            val downloaded = item.status == DownloadStatus.DOWNLOADED

            txtSubtitle.visibility = if (downloading || downloaded) View.VISIBLE else View.GONE
            button.visibility = if (notDownloaded || downloaded) View.VISIBLE else View.GONE
            progress.visibility = if (downloading || downloadStart) View.VISIBLE else View.GONE

            val btnText = if (notDownloaded) "Download" else "Open"
            button.text = btnText

            if (downloadStart) {
                progress.isIndeterminate = true
            } else if (downloading) {
                progress.isIndeterminate = false
                progress.max = 100
                progress.setProgress(item.percent, true)
            }

            txtSubtitle.text = if (downloading)
                "${item.progressSize} / ${item.totalSize}"
            else
                "${item.totalSize}"

            button.setOnClickListener {
                listener.onClickItem(item)
            }

        }
    }

}