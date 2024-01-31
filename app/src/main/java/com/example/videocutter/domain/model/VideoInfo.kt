package com.example.videocutter.domain.model

import com.example.baseapp.base.extension.getAppString
import com.example.videocutter.R

class VideoInfo(
    var id: Long? = null,

    var name: String? = null,

    var thumbnailUrl: String? = null,

    var createTime: String? = null,

    var duration: Long? = null,

    var count: Int? = null
) {
    fun getTime(): String {
        if (duration == null) return getAppString(R.string.time_default)
        val seconds: Long = duration!! / 1000 % 60 // Extract seconds from milliseconds
        val minutes: Long = duration!! / (1000 * 60) % 60 // Extract minutes from milliseconds
        return String.format("%02d:%02d", minutes, seconds)
    }
}
