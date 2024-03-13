package com.example.videocutter.domain.repo

import com.example.videocutter.domain.model.ITunes
import com.example.videocutter.domain.model.Music

interface IMusicRepo {
    fun getITunesList(): List<ITunes>

    fun getListMusicLocal(): List<Music>
}
