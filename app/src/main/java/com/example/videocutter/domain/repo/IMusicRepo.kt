package com.example.videocutter.domain.repo

import com.example.videocutter.domain.model.Music

interface IMusicRepo {
    fun getITunesList(): List<Music>

    fun getListMusicLocal(): List<Music>
}
