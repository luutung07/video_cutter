package com.example.videocutter.presentation.widget.addmusic

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.baseapp.base.extension.getAppDimension
import com.example.baseapp.base.extension.getAppString
import com.example.library_base.extension.FLOAT_DEFAULT
import com.example.library_base.extension.LONG_DEFAULT
import com.example.videocutter.R
import com.example.videocutter.common.extensions.convertTimeToString
import com.example.videocutter.common.extensions.getCoordinateXView
import kotlin.math.roundToLong

class AddMusicView constructor(
    ctx: Context, attrs: AttributeSet?
) : ConstraintLayout(ctx, attrs) {

    companion object {
        private const val MIN_DRAG = 150f
        private const val TAG = "AddMusicView"
    }

    private var clRoot: ConstraintLayout? = null

    private var tvLeft: TextView? = null
    private var clLeft: ConstraintLayout? = null

    private var tvCenter: TextView? = null

    private var tvRight: TextView? = null
    private var clRight: ConstraintLayout? = null

    private var vProgress: View? = null
    private var vBackGround: View? = null
    private var vRunProgress: View? = null

    private var widthScreen: Float? = null

    private var duration: Long? = 41000

    var listener: IAddMusicCallBack? = null

    init {
        LayoutInflater.from(ctx).inflate(R.layout.add_music_layout, this, true)
        initView()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        widthScreen =
            MeasureSpec.getSize(widthMeasureSpec) - 2 * getAppDimension(com.example.library_base.R.dimen.dimen_32)
        // 32 là do 20 margin và 12 do padding
    }

    private fun initView() {
        clRoot = findViewById(R.id.clAddMusicRoot)

        tvLeft = findViewById(R.id.tvAddMusicTimeLeft)
        clLeft = findViewById(R.id.clAddMusicLeft)

        tvCenter = findViewById(R.id.tvAddMusicTimeCenter)

        tvRight = findViewById(R.id.tvAddMusicTimeRight)
        clRight = findViewById(R.id.clAddMusicRight)

        vProgress = findViewById(R.id.vAddMusicProgress)
        vBackGround = findViewById(R.id.vAddMusicBackGround)
        vRunProgress = findViewById(R.id.vAddMusicRunProgresss)

        setEventView()
    }

    private fun setEventView() {
        dragLeft()
        dragRight()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun dragLeft() {
        var originMarginLeft = 0
        var coordinateX = 0f
        clLeft?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    originMarginLeft = (clLeft?.layoutParams as MarginLayoutParams).leftMargin
                    coordinateX = event.rawX
                }

                MotionEvent.ACTION_MOVE -> {
                    val newMarginLeft = event.rawX - coordinateX + originMarginLeft
                    updateLeft(newMarginLeft)
                }

                MotionEvent.ACTION_UP -> {
                    listener?.onHasCrop(false)
                }
            }
            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun dragRight() {
        var originMarginRight = 0
        var coordinateX = 0f
        clRight?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    originMarginRight = (clRight?.layoutParams as MarginLayoutParams).rightMargin
                    coordinateX = event.rawX
                }

                MotionEvent.ACTION_MOVE -> {
                    val newMarginRight = coordinateX - event.rawX + originMarginRight
                    updateRight(newMarginRight)
                }

                MotionEvent.ACTION_UP -> {
                    listener?.onHasCrop(false)
                }
            }
            true
        }
    }

    private fun updateLeft(leftMargin: Float, hasCallback: Boolean = true) {
        if (widthScreen == null) return

        val rightMarginClRight = (clRight?.layoutParams as MarginLayoutParams).rightMargin
        val max = widthScreen!! - rightMarginClRight

        val min = FLOAT_DEFAULT

        val newLeft = when {
            leftMargin <= min -> min
            leftMargin > max - MIN_DRAG -> max - MIN_DRAG
            else -> leftMargin
        }

        val newParam = clLeft?.layoutParams as MarginLayoutParams
        newParam.leftMargin = newLeft.toInt()
        clLeft?.layoutParams = newParam

        if (hasCallback) {
            val start = duration!! * newLeft / (widthScreen!!)
            val end = duration!! * max / widthScreen!!
            setText(newLeft, max)
            listener?.onCallBack(start, end)
            listener?.onHasCrop(true)
        }
    }

    private fun updateRight(right: Float, hasCallback: Boolean = true) {
        if (widthScreen == null) return

        val leftMarginClLeft = (clLeft?.layoutParams as MarginLayoutParams).leftMargin
        val max = widthScreen!! - leftMarginClLeft

        val min = FLOAT_DEFAULT

        var newRight = when {
            right < min -> min
            right > max - MIN_DRAG -> max - MIN_DRAG
            else -> right
        }

        val newParam = clRight?.layoutParams as MarginLayoutParams
        newParam.rightMargin = newRight.toInt()
        clRight?.layoutParams = newParam

        if (hasCallback) {
            newRight = widthScreen!! - newRight

            val start =
                duration!! * leftMarginClLeft / widthScreen!!
            val end = duration!! * newRight / widthScreen!!

            setText(leftMarginClLeft.toFloat(), newRight)
            listener?.onCallBack(start, end)
            listener?.onHasCrop(true)
        }
    }

    private fun updateVRunProgress(progress: Float) {
        val newParam = (vRunProgress?.layoutParams as MarginLayoutParams)
        newParam.leftMargin = progress.toInt()
        vRunProgress?.layoutParams = newParam
    }

    private fun setText(start: Float, end: Float) {
        val timeStart = duration!! * start / widthScreen!!
        tvLeft?.text = if (timeStart <= 1f) {
            getAppString(R.string.time_default)
        } else {
            timeStart.roundToLong().convertTimeToString()
        }

        val timeEnd = duration!! * end / widthScreen!!
        tvRight?.text = timeEnd.roundToLong().convertTimeToString()
    }

    fun setDuration(duration: Long) {
        this.duration = duration
        tvLeft?.text = getAppString(R.string.time_default)
        tvRight?.text = duration.convertTimeToString()
        requestLayout()
    }

    fun setCurrentDuration(position: Long) {
        if (widthScreen == null || duration == null) return
        tvCenter?.text = position.convertTimeToString()
        val leftMargin = widthScreen!! * position/duration!! + getAppDimension(com.example.library_base.R.dimen.dimen_10)
        updateVRunProgress(leftMargin)
    }

    fun reset() {
        updateLeft(FLOAT_DEFAULT, false)
        updateRight(FLOAT_DEFAULT, false)
        updateVRunProgress(FLOAT_DEFAULT)
        tvLeft?.text = getAppString(R.string.time_default)
        tvRight?.text = duration?.convertTimeToString()
    }

    interface IAddMusicCallBack {
        fun onCallBack(start: Float, end: Float)
        fun onHasCrop(isCrop: Boolean)
    }
}
