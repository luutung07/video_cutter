package com.example.videocutter.domain.repo

import android.graphics.Bitmap
import com.example.videocutter.domain.model.VideoInfo

interface IVideoRepo {
    fun getFolder(): List<VideoInfo>

    fun getFiles(id: Long?, page: Int, size: Int): List<VideoInfo>

    fun getFrameDetach(list: List<String>): List<Bitmap>
}
