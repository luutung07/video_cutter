package com.example.videocutter.presentation.display.model.video

import com.example.videocutter.domain.model.VideoInfo

data class VideoInfoDisplay(
    private var data: VideoInfo? = null,
    var isSelect: Boolean = false,
    var isMaxSelect: Boolean = false
) {
    fun getVideoInfo() = data ?: throw NullPointerException("video info null")
}
