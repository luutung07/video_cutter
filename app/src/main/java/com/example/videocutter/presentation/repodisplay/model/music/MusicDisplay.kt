package com.example.videocutter.presentation.repodisplay.model.music

data class MusicDisplay(
    var id: String? = null,
    var duration: Long? = null,
    var name: String? = null,
    var url: String? = null,
    var
)

enum class MUSIC_TYPE {
    ITUNES,
    LOCAL
}
