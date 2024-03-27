package com.example.videocutter.presentation.widget.cut.ver2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.baseapp.base.extension.getAppColor
import com.example.baseapp.base.extension.getAppDimension
import com.example.library_base.extension.FLOAT_DEFAULT
import com.example.videocutter.R
import com.example.videocutter.presentation.widget.cut.ver1.TrimView
import kotlin.math.roundToInt

class TrimViewVer2 constructor(
    ctx: Context,
    attrs: AttributeSet?
) : ConstraintLayout(ctx, attrs) {
    companion object {
        private const val TAG = "TrimView"
        private const val MIN_OFFSET = 5f
    }

    private var flStart: FrameLayout? = null
    private var vStart: View? = null
    private var tvStart: TextView? = null

    private var flEnd: FrameLayout? = null
    private var vEnd: View? = null
    private var tvEnd: TextView? = null

    private var vTop: View? = null
    private var vBottom: View? = null

    private var flProgress: FrameLayout? = null
    private var tvTrimProgress: TextView? = null

    private var widthScreen: Float? = null
    private var heightScreen: Float? = null
    private val paintOverlay = Paint().apply {
        color = getAppColor(R.color.over_lay)
    }

    private var duration: Long? = 410000

    init {
        LayoutInflater.from(ctx).inflate(R.layout.trim_video_ver2_layout, this, true)
        initView()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        widthScreen = MeasureSpec.getSize(widthMeasureSpec)
            .toFloat()
    }

    private fun initView() {
        flStart = findViewById(R.id.flTrimStartVer2)
        vStart = findViewById(R.id.vTrimStartVer2)
        tvStart = findViewById(R.id.tvTrimStartVer2)

        flEnd = findViewById(R.id.flTrimEndVer2)
        vEnd = findViewById(R.id.vTrimEndVer2)
        tvEnd = findViewById(R.id.tvTrimEndVer2)

        vTop = findViewById(R.id.vTrimTopVer2)
        vBottom = findViewById(R.id.vTrimBottomVer2)
        flProgress = findViewById(R.id.flTrimProgressVer2)
        tvTrimProgress = findViewById(R.id.tvTrimProgressVer2)

        setUpView()
    }

    private fun setUpView() {
        dragLeft()
        dragRight()
        setListFrame()
        dragProgress()
    }

    private fun setListFrame() {

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun dragProgress() {
        var coordinateX = 0f
        var leftOriginal = 0
        flProgress?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    coordinateX = event.rawX
                    leftOriginal = (flProgress?.layoutParams as MarginLayoutParams).leftMargin
                }

                MotionEvent.ACTION_MOVE -> {
                    val left = event.rawX - coordinateX + leftOriginal
                    updateProgress(left)
                }
            }
            true
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun dragRight() {
        var coordinateX = 0f
        var rightOriginal = 0
        flEnd?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    coordinateX = event.rawX
                    rightOriginal = (flEnd?.layoutParams as MarginLayoutParams).rightMargin
                }

                MotionEvent.ACTION_MOVE -> {
                    val rightMargin = coordinateX - event.rawX + rightOriginal
                    updateRight(rightMargin)
                }
            }
            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun dragLeft() {
        var coordinateX = 0f
        var leftOrigin = 0f
        flStart?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    coordinateX = event.rawX
                    leftOrigin = (v.layoutParams as MarginLayoutParams).leftMargin.toFloat()
                }

                MotionEvent.ACTION_MOVE -> {
                    val rawLeft = event.rawX - coordinateX + leftOrigin
                    updateLeft(rawLeft)
                }
            }
            true
        }
    }

    private fun updateRight(right: Float) {
        if (widthScreen == null) return
        val left = (flStart?.layoutParams as MarginLayoutParams).leftMargin
        val min = FLOAT_DEFAULT
        val max = widthScreen!! - left - getAppDimension(com.example.library_base.R.dimen.dimen_40)
        val newRight = when {
            right <= min -> min
            right > max - MIN_OFFSET -> max - MIN_OFFSET
            else -> right
        }

        val newParams = flEnd?.layoutParams as MarginLayoutParams
        newParams.rightMargin = newRight.toInt()
        flEnd?.layoutParams = newParams
    }

    private fun updateLeft(left: Float) {
        val right = (flEnd?.layoutParams as MarginLayoutParams).rightMargin
        val min = FLOAT_DEFAULT
        val max = widthScreen!! - right - getAppDimension(com.example.library_base.R.dimen.dimen_40)
        val newLeft = when {
            left <= min -> min
            left > max - MIN_OFFSET -> max - MIN_OFFSET
            else -> left
        }

        val newParams = flStart?.layoutParams as MarginLayoutParams
        newParams.leftMargin = newLeft.toInt()
        flStart?.layoutParams = newParams
    }

    private fun updateProgress(left: Float) {
        val right = (flEnd?.layoutParams as MarginLayoutParams).rightMargin
        val leftMargin = (flStart?.layoutParams as MarginLayoutParams).leftMargin
        val min = getAppDimension(com.example.library_base.R.dimen.dimen_10) + leftMargin
        val max = widthScreen!! - right - getAppDimension(com.example.library_base.R.dimen.dimen_30)
        val newLeft = when {
            left <= min -> min
            left > max - MIN_OFFSET -> max - MIN_OFFSET
            else -> left
        }

        val newParams = flProgress?.layoutParams as MarginLayoutParams
        newParams.leftMargin = newLeft.toInt()
        flProgress?.layoutParams = newParams
    }

}
