package com.example.videocutter.presentation.repodisplay.model.editvideo

import android.graphics.drawable.Drawable
import com.example.baseapp.base.extension.getAppDrawable
import com.example.baseapp.base.extension.getAppString
import com.example.library_base.extension.STRING_DEFAULT
import com.example.videocutter.R
import com.example.videocutter.presentation.repodisplay.model.editvideo.FEATURE_TYPE

data class FeatureEditVideoDisplay(
    var featureType: FEATURE_TYPE? = null
) {
    fun getName(): String {
        return when (featureType) {
            FEATURE_TYPE.CROP -> getAppString(R.string.feature_crop)
            FEATURE_TYPE.CUT -> getAppString(R.string.feature_cut)
            FEATURE_TYPE.SPEED -> getAppString(R.string.feature_speed)
            FEATURE_TYPE.FILTER -> getAppString(R.string.feature_filter)
            FEATURE_TYPE.MUSIC -> getAppString(R.string.feature_music)
            FEATURE_TYPE.ROTATE -> getAppString(R.string.feature_rotate)
            else -> STRING_DEFAULT
        }
    }

    fun getIcon(): Drawable? {
        return when (featureType) {
            FEATURE_TYPE.CROP -> getAppDrawable(R.drawable.ic_crop)
            FEATURE_TYPE.CUT -> getAppDrawable(R.drawable.ic_cut)
            FEATURE_TYPE.SPEED -> getAppDrawable(R.drawable.ic_speed)
            FEATURE_TYPE.FILTER -> getAppDrawable(R.drawable.ic_filter)
            FEATURE_TYPE.MUSIC -> getAppDrawable(R.drawable.ic_music)
            FEATURE_TYPE.ROTATE -> getAppDrawable(R.drawable.ic_rotate)
            else -> null
        }
    }
}

