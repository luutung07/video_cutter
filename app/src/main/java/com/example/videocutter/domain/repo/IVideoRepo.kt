package com.example.videocutter.domain.repo

import com.example.videocutter.domain.model.VideoInfo

interface IVideoRepo {
    fun getFolder(): List<VideoInfo>

    fun getFiles(id: Long?, page: Int, size: Int): List<VideoInfo>
}
