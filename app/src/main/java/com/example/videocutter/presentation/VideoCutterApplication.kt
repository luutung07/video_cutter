package com.example.videocutter.presentation

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.media3.common.util.UnstableApi
import com.example.baseapp.base.extension.getAppString
import com.example.library_base.extension.setApplication
import com.example.videocutter.R
import dagger.hilt.android.HiltAndroidApp

@UnstableApi
@HiltAndroidApp
class VideoCutterApplication : Application() {

    companion object {
        private const val CHANEL_ID = "CHANEL_ID"
    }

    override fun onCreate() {
        super.onCreate()
        AppPreferences.init(this)
        setApplication(this)
        createNotify()
    }

    private fun createNotify() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANEL_ID,
                getAppString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
