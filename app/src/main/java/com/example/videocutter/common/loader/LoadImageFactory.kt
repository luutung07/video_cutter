package com.example.videocutter.common.loader

import com.example.videocutter.common.loader.image.ILoadImage
import com.example.videocutter.common.loader.image.LoadImageImpl

object LoadImageFactory {
    private val loader by lazy { LoadImageImpl() }

    init {

    }

    fun getLoadImage(): ILoadImage {
        return loader
    }

}
