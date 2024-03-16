package com.example.videocutter.presentation.display.model.music

import com.example.videocutter.domain.model.Music

data class MusicDisplay(
    var type: MUSIC_TYPE? = null,
    var data: Music? = null,
    var isSelect: Boolean = false,
    var isShowTrimMusic: Boolean = false,
    var isDownLoaded: Boolean? = null,
    var isPlay: Boolean = false,
    var currentPosition: Long? = null
) {
    fun getMusic() = data ?: throw NullPointerException("data music null")
}

enum class MUSIC_TYPE {
    ITUNES,
    LOCAL
}

