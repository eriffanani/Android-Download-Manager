package com.erif.filedownloaderdemo.example.multiple

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.erif.filedownloaderdemo.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator

class AdapterMultipleDownload(
    private val list: MutableList<ModelItemMultiple>
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
        list[position].status = DownloadStatus.DOWNLOAD_START
        notifyItemChanged(position)
    }

    fun downloading(position: Int) {
        val item = list[position]
        val currentStatus = item.status
        if (currentStatus != DownloadStatus.DOWNLOADING) {
            item.status = DownloadStatus.DOWNLOADING
            notifyItemChanged(position)
        }
    }

    fun updateProgress(position: Int) {
        list[position].progress = 20
        notifyItemChanged(position)
    }

    class Holder(
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
                val subtitle = "${item.progress} / ${item.totalSize}"
                txtSubtitle.text = subtitle

                progress.isIndeterminate = false
                progress.max = 100
                progress.setProgress(item.progress, true)
            }
        }
    }

}