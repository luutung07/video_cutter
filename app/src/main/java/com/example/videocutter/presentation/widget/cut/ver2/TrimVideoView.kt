package com.example.videocutter.presentation.widget.cut.ver2

import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.baseapp.base.extension.getAppDimension
import com.example.videocutter.R

class TrimVideoView constructor(
    private val ctx: Context,
    attributeSet: AttributeSet?
) : ConstraintLayout(ctx, attributeSet) {
    private var trimView: TrimViewVer2? = null
    private var llFrame: LinearLayout? = null

    init {
        LayoutInflater.from(ctx).inflate(R.layout.trim_video_view, this, true)
        initView()
    }

    private fun initView() {
        trimView = findViewById(R.id.trimViewVideo)
        llFrame = findViewById(R.id.llTrimVideoViewFrame)
    }

    fun setFrame(list: List<Bitmap>) {
        list.forEach {
            val image = ImageView(ctx)
            image.layoutParams = LayoutParams(
                getAppDimension(com.example.library_base.R.dimen.dimen_50).toInt(),
                getAppDimension(com.example.library_base.R.dimen.dimen_50).toInt()
            )
            image.scaleType = ImageView.ScaleType.FIT_XY
            image.setImageBitmap(it)
            llFrame?.addView(image)
        }
    }
}
