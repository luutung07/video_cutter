package com.example.library_base.common.loader.image

import android.graphics.drawable.Drawable
import android.widget.ImageView

interface IImageLoader {
    fun loadImage(
        view: ImageView,
        url: String?,
        placeHolder: Drawable? = null,
        ignoreCache: Boolean = false
    )

    fun loadImage(
        view: ImageView,
        drawable: Drawable?,
        placeHolder: Drawable? = null,
        ignoreCache: Boolean = false
    )
}
