package com.example.videocutter.presentation.widget.timeline

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.baseapp.base.extension.setOnSafeClick
import com.example.videocutter.R

class TimelineView constructor(
    ctx: Context,
    attrs: AttributeSet?
) : ConstraintLayout(ctx, attrs) {

    private var ivLeft: ImageView? = null
    private var onActionLeft: (() -> Unit)? = null
    private var tvLeft: TextView? = null

    private var ivRight: ImageView? = null
    private var tvRight: TextView? = null
    private var onActionRight: (() -> Unit)? = null

    private var seekbarProgress: SeekBar? = null

    private var tvCenter: TextView? = null

    var listener: ITimeLineCallBack? = null

    init {
        LayoutInflater.from(ctx).inflate(R.layout.time_line_layout, this, true)
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        ivLeft = findViewById(R.id.ivTimeLineLeft)
        ivRight = findViewById(R.id.ivTimeLineRight)
        seekbarProgress = findViewById(R.id.sbTimeLine)
        tvRight = findViewById(R.id.tvTimeLineEnd)
        tvLeft = findViewById(R.id.tvTimeLineStart)
        tvCenter = findViewById(R.id.tvTimeLineCenter)
        setUpView()
    }

    private fun setUpView() {
        seekbarProgress?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                listener?.onProgressChanged(seekBar, progress, fromUser)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                listener?.onStartTrackingTouch(seekBar)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                listener?.onStopTrackingTouch(seekBar)
            }
        })

        ivLeft?.setOnSafeClick {
            onActionLeft?.invoke()
        }
    }

    fun setTvStart(label: CharSequence?) {
        tvLeft?.text = label
    }

    fun setTvCenter(label: CharSequence?) {
        tvCenter?.text = label
    }

    fun setTvEnd(label: CharSequence?) {
        tvRight?.text = label
    }

    fun setImageDrawableIcLeft(drawable: Drawable?) {
        ivLeft?.setImageDrawable(drawable)
    }

    fun setOnActionLeft(action: () -> Unit) {
        onActionLeft = action
    }

    fun setOnActionRight(action: () -> Unit) {
        onActionRight = action
    }

    fun setProgressSeekbar(progress: Int) {
        seekbarProgress?.progress = progress
    }

    fun setMaxProgress(max: Int) {
        seekbarProgress?.max = max
    }

    interface ITimeLineCallBack {
        fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
        fun onStartTrackingTouch(seekBar: SeekBar?) {}
        fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }
}
