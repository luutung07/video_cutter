package com.example.videocutter.presentation.display.model.editvideo

import android.graphics.drawable.Drawable
import com.example.baseapp.base.extension.getAppDrawable
import com.example.baseapp.base.extension.getAppString
import com.example.library_base.extension.STRING_DEFAULT
import com.example.videocutter.R
import com.example.videocutter.presentation.widget.crop.CROP_TYPE

data class CropDisplay(
    var cropType: CROP_TYPE? = null,
    var isSelect: Boolean = false
) {
    fun getName(): String {
        return when (cropType) {
            CROP_TYPE.TYPE_CUSTOM -> getAppString(R.string.crop_custom)
            CROP_TYPE.TYPE_INSTAGRAM -> getAppString(R.string.crop_instagram)
            CROP_TYPE.TYPE_4_5 -> getAppString(R.string.crop4x5)
            CROP_TYPE.TYPE_5_4 -> getAppString(R.string.crop5x4)
            CROP_TYPE.TYPE_9_16 -> getAppString(R.string.crop9x16)
            CROP_TYPE.TYPE_16_9 -> getAppString(R.string.crop16x9)
            CROP_TYPE.TYPE_3_2 -> getAppString(R.string.crop3x2)
            CROP_TYPE.TYPE_2_3 -> getAppString(R.string.crop2x3)
            CROP_TYPE.TYPE_4_3 -> getAppString(R.string.crop4x3)
            CROP_TYPE.TYPE_3_4 -> getAppString(R.string.crop3x4)
            else -> STRING_DEFAULT
        }
    }

    fun getIcon(): Drawable? {
        return if (!isSelect){
            when(cropType){
                CROP_TYPE.TYPE_CUSTOM -> getAppDrawable(R.drawable.ic_crop_custom)
                CROP_TYPE.TYPE_INSTAGRAM -> getAppDrawable(R.drawable.ic_crop_1x1)
                CROP_TYPE.TYPE_4_5 -> getAppDrawable(R.drawable.ic_crop_4x5)
                CROP_TYPE.TYPE_5_4 -> getAppDrawable(R.drawable.ic_crop_5x4)
                CROP_TYPE.TYPE_9_16 -> getAppDrawable(R.drawable.ic_crop_9x16)
                CROP_TYPE.TYPE_16_9 -> getAppDrawable(R.drawable.ic_crop_16x9)
                CROP_TYPE.TYPE_3_2 -> getAppDrawable(R.drawable.ic_crop_3x2)
                CROP_TYPE.TYPE_2_3 -> getAppDrawable(R.drawable.ic_crop_4x3)
                CROP_TYPE.TYPE_4_3 -> getAppDrawable(R.drawable.ic_crop_4x3)
                CROP_TYPE.TYPE_3_4 -> getAppDrawable(R.drawable.ic_crop_3x4)
                else -> null
            }
        }else{
            when(cropType){
                CROP_TYPE.TYPE_CUSTOM -> getAppDrawable(R.drawable.ic_crop_select_custom)
                CROP_TYPE.TYPE_INSTAGRAM -> getAppDrawable(R.drawable.ic_crop_select_1x1)
                CROP_TYPE.TYPE_4_5 -> getAppDrawable(R.drawable.ic_crop_select_4x5)
                CROP_TYPE.TYPE_5_4 -> getAppDrawable(R.drawable.ic_crop_select_5x4)
                CROP_TYPE.TYPE_9_16 -> getAppDrawable(R.drawable.ic_crop_select_9x16)
                CROP_TYPE.TYPE_16_9 -> getAppDrawable(R.drawable.ic_crop_select_16x9)
                CROP_TYPE.TYPE_3_2 -> getAppDrawable(R.drawable.ic_crop_select_3x2)
                CROP_TYPE.TYPE_2_3 -> getAppDrawable(R.drawable.ic_crop_select_2x3)
                CROP_TYPE.TYPE_4_3 -> getAppDrawable(R.drawable.ic_crop_select_4x3)
                CROP_TYPE.TYPE_3_4 -> getAppDrawable(R.drawable.ic_crop_select_3x4)
                else -> null
            }
        }
    }
}
