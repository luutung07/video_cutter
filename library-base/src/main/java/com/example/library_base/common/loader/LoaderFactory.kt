package com.example.library_base.common.loader

import com.example.library_base.common.loader.image.GlideImageLoaderImpl
import com.example.library_base.common.loader.image.IImageLoader

object LoaderFactory {
    private val imageLoader = GlideImageLoaderImpl()

    fun glide(): IImageLoader {
        return imageLoader
    }
}
