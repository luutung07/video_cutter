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

    private val WIDTH_MOVE_OFFSET: Float = getAppDimension(com.example.library_base.R.dimen.dimen_6)

    private var clRoot: ConstraintLayout? = null

    private var tvLeft: TextView? = null
    private var clLeft: ConstraintLayout? = null

    private var tvCenter: TextView? = null

    private var tvRight: TextView? = null
    private var clRight: ConstraintLayout? = null

    private var vProgress: View? = null
    private var vBackGround: View? = null

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
            MeasureSpec.getSize(widthMeasureSpec)
                .toFloat() - 2 * getAppDimension(com.example.library_base.R.dimen.dimen_20)
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
            }
            true
        }
    }

    private fun updateLeft(leftMargin: Float) {
        val max = clRight!!.getCoordinateXView() - clRoot!!.getCoordinateXView()
        val min = getAppDimension(com.example.library_base.R.dimen.dimen_20)
        val newLeft = when {
            leftMargin < min -> min
            leftMargin > max - MIN_DRAG -> max - MIN_DRAG
            else -> leftMargin
        }
        val newParam = clLeft?.layoutParams as MarginLayoutParams
        newParam.leftMargin = newLeft.toInt()
        clLeft?.layoutParams = newParam

        val coordinateRightX =
            clRight!!.getCoordinateXView() - getAppDimension(com.example.library_base.R.dimen.dimen_30) - clRight!!.width
        val start = duration!! * newLeft / widthScreen!!
        val end =
            duration!! * coordinateRightX / widthScreen!!

        setText(
            newLeft - getAppDimension(com.example.library_base.R.dimen.dimen_10),
            coordinateRightX
        )
        listener?.onCallBack(start, end)
    }

    private fun updateRight(right: Float) {
        val min = getAppDimension(com.example.library_base.R.dimen.dimen_20)
        val max = clLeft!!.getCoordinateXView() - clRoot!!.getCoordinateXView() + MIN_DRAG
        var newRight = when {
            right < min -> min.toFloat()
            right > widthScreen!! - max -> widthScreen!! - max.toFloat()
            else -> right
        }

        val newParam = clRight?.layoutParams as MarginLayoutParams
        newParam.rightMargin = newRight.toInt()
        clRight?.layoutParams = newParam

        newRight = widthScreen!! - newRight + getAppDimension(com.example.library_base.R.dimen.dimen_10)

        val coordinateXLeft =
            clLeft!!.getCoordinateXView() - getAppDimension(com.example.library_base.R.dimen.dimen_30) - clLeft!!.width
        val start =
            duration!! * coordinateXLeft / widthScreen!!
        val end = duration!! * newRight / widthScreen!!

        setText(coordinateXLeft, newRight)
        listener?.onCallBack(start, end)
    }

    private fun setText(start: Float, end: Float) {
        val timeStart = duration!! * start / widthScreen!!
        tvLeft?.text = timeStart.roundToLong().convertTimeToString()

        val timeEnd = duration!! * end / widthScreen!!
        tvRight?.text = timeEnd.roundToLong().convertTimeToString()
    }

    private fun setTextLeft(start: Float) {

    }

    fun setTextRight(end: Float) {

    }

    fun setDuration(duration: Long) {
        this.duration = duration
        tvLeft?.text = "00:01"
        tvRight?.text = duration.convertTimeToString()
        requestLayout()
    }

    fun setCurrentDuration(position: Long) {
        tvCenter?.text = position.convertTimeToString()
    }

    interface IAddMusicCallBack {
        fun onCallBack(start: Float, end: Float)
    }
}
