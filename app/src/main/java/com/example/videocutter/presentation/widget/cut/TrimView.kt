package com.example.videocutter.presentation.widget.cut

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baseapp.base.extension.getAppColor
import com.example.baseapp.base.extension.getAppDimension
import com.example.baseapp.base.extension.getAppString
import com.example.library_base.extension.FLOAT_DEFAULT
import com.example.library_base.extension.INT_DEFAULT
import com.example.videocutter.R
import com.example.videocutter.common.extensions.convertTimeToString
import com.example.videocutter.common.extensions.getCoordinateYView
import com.example.videocutter.presentation.display.model.editvideo.DetachFrameDisplay
import com.example.videocutter.presentation.widget.ExtractVideoAdapter
import com.example.videocutter.presentation.widget.recyclerview.COLLECTION_MODE
import com.example.videocutter.presentation.widget.recyclerview.CollectionView
import java.util.concurrent.Flow
import kotlin.math.roundToInt

class TrimView constructor(
    ctx: Context,
    attrs: AttributeSet?
) : ConstraintLayout(ctx, attrs) {

    companion object {
        private const val TAG = "TrimView"
        private const val MIN_OFFSET = 250f
    }

    private var flStart: FrameLayout? = null
    private var vStart: View? = null
    private var tvStart: TextView? = null

    private var flEnd: FrameLayout? = null
    private var vEnd: View? = null
    private var tvEnd: TextView? = null

    private var vTop: View? = null
    private var vBottom: View? = null

    private var vTrimProgress: View? = null

    private var widthScreen: Float? = null
    private var heightScreen: Float? = null
    private val paintOverlay = Paint().apply {
        color = getAppColor(R.color.over_lay)
    }

    private var duration: Long? = 410000

    private var cvTrim: CollectionView? = null
    private val adapter by lazy { ExtractVideoAdapter() }
    private var firstItem = INT_DEFAULT
    private var lastItem = INT_DEFAULT
    private var totalItem = INT_DEFAULT

    var listener: ITrimCallBack? = null

    init {
        LayoutInflater.from(ctx).inflate(R.layout.trim_video_layout, this, true)
        initView()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        widthScreen = MeasureSpec.getSize(widthMeasureSpec)
            .toFloat() - 2 * getAppDimension(com.example.library_base.R.dimen.dimen_36)
        heightScreen = MeasureSpec.getSize(heightMeasureSpec).toFloat()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        drawOutline(canvas)
    }

    private fun drawOutline(canvas: Canvas) {
        val start = (flStart?.layoutParams as MarginLayoutParams).leftMargin
        val end = (flEnd?.layoutParams as MarginLayoutParams).rightMargin
        val top = (tvStart?.height
            ?: INT_DEFAULT) + getAppDimension(com.example.library_base.R.dimen.dimen_8) // 8 là do trừ đi view top
        val bottom = (tvStart?.height
            ?: INT_DEFAULT) + getAppDimension(com.example.library_base.R.dimen.dimen_8) + (flEnd?.height
            ?: INT_DEFAULT)

        canvas.drawRect(
            getAppDimension(com.example.library_base.R.dimen.dimen_32),
            top,
            start.toFloat() + getAppDimension(com.example.library_base.R.dimen.dimen_32),
            bottom,
            paintOverlay
        )

        val offsetLeftRight = if (end == 0) {
            getAppDimension(com.example.library_base.R.dimen.dimen_32)
        } else {
            getAppDimension(com.example.library_base.R.dimen.dimen_40)
        }
        canvas.drawRect(
            widthScreen!! - end + offsetLeftRight,
            top,
            widthScreen!! + getAppDimension(com.example.library_base.R.dimen.dimen_32),
            bottom,
            paintOverlay
        )
    }

    private fun initView() {
        flStart = findViewById(R.id.flTrimStart)
        vStart = findViewById(R.id.vTrimStart)
        tvStart = findViewById(R.id.tvTrimStart)

        flEnd = findViewById(R.id.flTrimEnd)
        vEnd = findViewById(R.id.vTrimEnd)
        tvEnd = findViewById(R.id.tvTrimEnd)

        vTop = findViewById(R.id.vTrimTop)
        vBottom = findViewById(R.id.vTrimBottom)
        vTrimProgress = findViewById(R.id.vTrimProgress)

        cvTrim = findViewById(R.id.cvTrim)

        setUpView()
    }


    private fun setUpView() {
        dragLeft()
        dragRight()
        setUpAdapter()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun dragLeft() {
        var coordinateX = 0f
        var leftStartOriginal = 0
        flStart?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    coordinateX = event.rawX
                    leftStartOriginal = (flStart?.layoutParams as MarginLayoutParams).leftMargin
                }

                MotionEvent.ACTION_MOVE -> {
                    val leftMargin = event.rawX - coordinateX + leftStartOriginal
                    updateLeft(leftMargin)
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

    private fun updateLeft(left: Float) {
        if (widthScreen == null) return
        val right = (flEnd?.layoutParams as MarginLayoutParams).rightMargin
        val min = FLOAT_DEFAULT
        val max = widthScreen!! - right
        val newLeft = when {
            left <= min -> min
            left > max - MIN_OFFSET -> max - MIN_OFFSET
            else -> left
        }

        val newParams = flStart?.layoutParams as MarginLayoutParams
        newParams.leftMargin = newLeft.toInt()
        flStart?.layoutParams = newParams

        val distance = lastItem - firstItem
        val indexRvStart = (distance * newLeft / widthScreen!!).roundToInt() + firstItem
        val indexRvEnd =
            (distance * (widthScreen!! - right) / widthScreen!!).roundToInt() + firstItem

        val start = duration!! * indexRvStart / totalItem
        val end = duration!! * indexRvEnd / totalItem
        Log.d(
            TAG,
            "updateLeft: [first = $firstItem-last = $lastItem] - [indexS = $indexRvStart-indexE = $indexRvEnd]-[start = $start - end = $end]"
        )
        setText(start, end)
        listener?.onTrim(start, end)
    }

    private fun updateRight(right: Float) {
        if (widthScreen == null) return
        val left = (flStart?.layoutParams as MarginLayoutParams).leftMargin
        val min = FLOAT_DEFAULT
        val max = widthScreen!! - left
        val newRight = when {
            right <= min -> min
            right > max - MIN_OFFSET -> max - MIN_OFFSET
            else -> right
        }

        val newParams = flEnd?.layoutParams as MarginLayoutParams
        newParams.rightMargin = newRight.toInt()
        flEnd?.layoutParams = newParams

        val distance = lastItem - firstItem
        val indexRvStart = (distance * left / widthScreen!!).roundToInt() + firstItem
        val indexRvEnd =
            (distance * (widthScreen!! - newRight) / widthScreen!!).roundToInt() + firstItem

        val start = duration!! * indexRvStart / totalItem
        val end = duration!! * indexRvEnd / totalItem

        Log.d(
            TAG,
            "onScrolled: [first = $firstItem-last = $lastItem] - [indexS = $indexRvStart-indexE = $indexRvEnd]-[start = $start - end = $end]"
        )
        setText(start, end)
        listener?.onTrim(start, end)
    }

    private fun setText(start: Long, end: Long) {
        tvStart?.text = start.toLong().convertTimeToString()
        tvEnd?.text = end.toLong().convertTimeToString()
    }

    fun updateProgress(progress: Long, end: Long) {
        val left = widthScreen!! * progress / end
        Log.d(TAG, "updateProgress: $left")

        val newParam = vTrimProgress?.layoutParams as MarginLayoutParams
        newParam.leftMargin =
            (left + getAppDimension(com.example.library_base.R.dimen.dimen_20)).toInt()
        vTrimProgress?.layoutParams = newParam
    }

    fun setDuration(duration: Long) {
        this.duration = duration
        tvEnd?.text = duration.convertTimeToString()
        tvStart?.text = getAppString(R.string.time_default)
    }

    fun setUpAdapter() {
        cvTrim?.setAdapter(adapter)
        cvTrim?.setLayoutManager(COLLECTION_MODE.HORIZONTAL)

        cvTrim?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItem =
                    (recyclerView.layoutManager as? LinearLayoutManager)?.itemCount ?: INT_DEFAULT
                firstItem =
                    (recyclerView.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
                        ?: INT_DEFAULT
                lastItem =
                    (recyclerView.layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition()
                        ?: INT_DEFAULT

                if (widthScreen == null) return
                val distance = lastItem - firstItem
                val left = (flStart?.layoutParams as MarginLayoutParams).leftMargin
                val right = widthScreen!! - (flEnd?.layoutParams as MarginLayoutParams).rightMargin

                val indexRvStart = (distance * left / widthScreen!!).roundToInt() + firstItem
                val indexRvEnd = (distance * right / widthScreen!!).roundToInt() + firstItem

                val start = duration!! * indexRvStart / totalItem
                val end = duration!! * indexRvEnd / totalItem
                Log.d(
                    TAG,
                    "onScrolled: [first = $firstItem-last = $lastItem] - [indexS = $indexRvStart-indexE = $indexRvEnd]-[start = $start - end = $end]"
                )
                setText(start, end)
                listener?.onTrim(start, end)
            }
        })
    }

    fun setListFrame(list: List<DetachFrameDisplay>) {
        adapter.submitList(list)
    }

    interface ITrimCallBack {
        fun onTrim(start: Long, end: Long)
    }
}
