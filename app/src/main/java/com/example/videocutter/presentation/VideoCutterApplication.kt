package com.example.videocutter.presentation

import android.app.Application
import com.example.library_base.extension.setApplication

class VideoCutterApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        setApplication(this)
    }
}
