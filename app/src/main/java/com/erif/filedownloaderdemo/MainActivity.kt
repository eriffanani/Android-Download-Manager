package com.erif.filedownloaderdemo

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.erif.filedownloaderdemo.example.ActSingleFileDownload
import com.erif.filedownloaderdemo.example.multiple.ActMultipleFileDownload
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setSupportActionBar(findViewById(R.id.toolbar))

        val btnSingle: MaterialButton = findViewById(R.id.btnSingle)
        btnSingle.setOnClickListener {
            intentTo(ActSingleFileDownload::class.java)
        }

        val btnMultiple: MaterialButton = findViewById(R.id.btnMultiple)
        btnMultiple.setOnClickListener {
            intentTo(ActMultipleFileDownload::class.java)
        }

    }

    private fun intentTo(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
    }

}