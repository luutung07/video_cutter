package com.example.library_base.common.loader.image

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class GlideImageLoaderImpl : IImageLoader {
    override fun loadImage(
        view: ImageView,
        url: String?,
        placeHolder: Drawable?,
        ignoreCache: Boolean
    ) {
        try {
            Glide.with(view)
                .load(url)
                .placeholder(placeHolder)
                .skipMemoryCache(ignoreCache)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(view)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun loadImage(
        view: ImageView,
        drawable: Drawable?,
        placeHolder: Drawable?,
        ignoreCache: Boolean
    ) {
        try {
            Glide.with(view)
                .load(drawable)
                .placeholder(placeHolder)
                .skipMemoryCache(ignoreCache)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(view)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
