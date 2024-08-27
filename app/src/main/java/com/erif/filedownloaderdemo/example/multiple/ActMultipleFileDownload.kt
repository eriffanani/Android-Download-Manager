package com.erif.filedownloaderdemo.example.multiple

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.erif.filedownloaderdemo.R

class ActMultipleFileDownload : AppCompatActivity() {

    private lateinit var adapter: AdapterMultipleDownload

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

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.layoutAnimation = AnimationUtils.loadLayoutAnimation(
            this, R.anim.layout_anim_fade_in
        )
        clearRecyclerAnim(recyclerView)
        val urls = arrayOf(
            "https://link.testfile.org/iK7sKT",
            "https://jsoncompare.org/LearningContainer/SampleFiles/Video/MP4/Sample-MP4-Video-File-Download.mp4",
            "https://link.testfile.org/aYr11v"
        )

        val list: MutableList<ModelItemMultiple> = ArrayList()
        urls.forEachIndexed { idx, url ->
            val item = ModelItemMultiple(
                1, url, "This is file name ${idx+1}",
                0, 0
            )
            list.add(item)
        }
        adapter = AdapterMultipleDownload(list)
        Handler(Looper.getMainLooper()).postDelayed({
            recyclerView.adapter = adapter
            recyclerView.scheduleLayoutAnimation()
        }, 700)

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

}