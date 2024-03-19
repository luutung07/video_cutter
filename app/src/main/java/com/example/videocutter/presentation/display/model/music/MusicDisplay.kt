package com.example.videocutter.presentation.display.model.music

import com.example.videocutter.domain.model.Music

data class MusicDisplay(
    var type: MUSIC_TYPE? = null,
    var data: Music? = null,
    var musicState: MusicState = MusicState()
) {
    fun getMusic() = data ?: throw NullPointerException("data music null")

    fun getState() = musicState
}

data class MusicState(
    var isSelect: Boolean = false,
    var isShowTrimMusic: Boolean = false,
    var isDownLoaded: Boolean? = null,
    var isPlay: Boolean = false,
    var currentPosition: Long? = null,
    var start: Long? = null,
    var end: Long? = null
)

enum class MUSIC_TYPE {
    ITUNES,
    LOCAL
}

