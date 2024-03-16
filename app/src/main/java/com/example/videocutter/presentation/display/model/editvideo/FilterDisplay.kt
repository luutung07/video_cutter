package com.example.videocutter.presentation.display.model.editvideo

import android.graphics.drawable.Drawable
import com.example.baseapp.base.extension.getAppDrawable
import com.example.baseapp.base.extension.getAppString
import com.example.videocutter.R

data class FilterDisplay(
    var filterType: FILTER_TYPE? = null,
    var isSelect: Boolean? = null
) {
    fun getTitle(): String {
        return when (filterType) {
            FILTER_TYPE.ORIGINAL -> getAppString(R.string.filter_original)
            FILTER_TYPE.SPRING -> getAppString(R.string.filter_spring)
            FILTER_TYPE.SUMMER -> getAppString(R.string.filter_summer)
            FILTER_TYPE.FALL -> getAppString(R.string.filter_fall)
            FILTER_TYPE.WINTER -> getAppString(R.string.filter_winter)
            else -> getAppString(R.string.filter_original)
        }
    }

    fun getImage(): Drawable?{
        return when (filterType) {
            FILTER_TYPE.ORIGINAL -> getAppDrawable(R.drawable.img_filter_original)
            FILTER_TYPE.SPRING -> getAppDrawable(R.drawable.img_filter_spring)
            FILTER_TYPE.SUMMER -> getAppDrawable(R.drawable.img_filter_summer)
            FILTER_TYPE.FALL -> getAppDrawable(R.drawable.img_filter_original)
            FILTER_TYPE.WINTER -> getAppDrawable(R.drawable.img_filter_original)
            else -> getAppDrawable(R.drawable.img_filter_original)
        }
    }
}
