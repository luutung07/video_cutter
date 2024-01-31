package com.example.videocutter.presentation

import android.app.Application
import com.example.library_base.extension.setApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VideoCutterApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        setApplication(this)
    }
}
