package com.example.videocutter.presentation.widget.cut

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.baseapp.base.extension.getAppColor
import com.example.baseapp.base.extension.getAppDrawable
import com.example.baseapp.base.extension.show
import com.example.library_base.extension.STRING_DEFAULT
import com.example.videocutter.R
import com.example.videocutter.common.extensions.getCoordinateXView
import com.example.videocutter.presentation.widget.recyclerview.COLLECTION_MODE
import com.example.videocutter.presentation.widget.recyclerview.CollectionView
import com.example.videocutter.presentation.widget.video.ExtractVideoAdapter
import kotlin.math.abs

class TrimVideo constructor(
    private val ctx: Context,
    attrs: AttributeSet?
) : ConstraintLayout(ctx, attrs) {

    companion object {
        private const val TAG = "TrimVideo"
        private const val MIN_DISTANCE = 200f
    }

    private var ivTrimStart: ImageView? = null
    private var coordinateXStart = 0f
    private var originalLeftMarginIvTrimStart = 0f
    private var newParamIvTrimStart: MarginLayoutParams? = null

    private var ivTrimEnd: ImageView? = null
    private var originalRightMarginIvTrimEnd = 0f
    private var newParamIvTrimEnd: MarginLayoutParams? = null
    private var coordinateXEnd = 0f

    private var vTop: View? = null
    private var newParamTop: MarginLayoutParams? = null

    private var vBottom: View? = null
    private var newParamBottom: MarginLayoutParams? = null

    private var ivTimeline: ImageView? = null

    private var cvFrame: CollectionView? = null

    private var widthScreen: Float = 0f
    private var heightScreen: Float = 0f
    private var coordinateXLimitEnd = 0f
    private var coordinateXLimitStart = 0f

    private val paintOverlay = Paint().apply {
        color = getAppColor(R.color.over_lay)
    }

    private val listFrame: MutableList<Bitmap> = arrayListOf()
    private val extractAdapter by lazy { ExtractVideoAdapter() }

    private var threadExtractFrameVideo: Thread? = null

    init {
        LayoutInflater.from(ctx).inflate(R.layout.trim_video_layout, this, true)
        initView()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        widthScreen = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        heightScreen = MeasureSpec.getSize(heightMeasureSpec).toFloat()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        coordinateXLimitStart = ivTrimStart!!.getCoordinateXView().toFloat() - ivTrimStart!!.width
        coordinateXLimitEnd = ivTrimEnd!!.getCoordinateXView().toFloat() - ivTrimEnd!!.width
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        drawOutline(canvas)
    }

    private fun drawOutline(canvas: Canvas) {
        Log.d(
            TAG,
            "drawOutline: [newParamIvTrimStart!!.leftMargin = ${newParamIvTrimStart!!.leftMargin} -- [width = $widthScreen]-- [height = $heightScreen] -- [newParamIvTrimEnd!!.rightMargin = ${newParamIvTrimEnd!!.rightMargin}]"
        )
        canvas.drawRect(
            0f,
            0f,
            newParamIvTrimStart!!.leftMargin.toFloat(),
            heightScreen,
            paintOverlay
        )
        canvas.drawRect(
            widthScreen - newParamIvTrimEnd!!.rightMargin,
            0f,
            widthScreen,
            heightScreen,
            paintOverlay
        )
    }

    private fun initView() {
        ivTrimStart = findViewById(R.id.ivTrimStart)
        ivTrimEnd = findViewById(R.id.ivTrimEnd)
        vTop = findViewById(R.id.vTrimTop)
        vBottom = findViewById(R.id.vTrimBottom)
        ivTimeline = findViewById(R.id.ivTrimVideoTimeline)
        cvFrame = findViewById(R.id.cvTrimFrame)

        newParamIvTrimStart = ivTrimStart!!.layoutParams as MarginLayoutParams
        newParamIvTrimEnd = ivTrimEnd!!.layoutParams as MarginLayoutParams
        newParamTop = vTop!!.layoutParams as MarginLayoutParams
        newParamBottom = vBottom!!.layoutParams as MarginLayoutParams

        setEventView()
        setUpAdapter()
    }

    private fun setUpAdapter() {
        cvFrame?.setAdapter(adapter = extractAdapter)
        cvFrame?.setLayoutManager(COLLECTION_MODE.HORIZONTAL)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setEventView() {
        ivTrimStart?.setOnTouchListener { iv, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d(TAG, "[x = ${event.x}] -- [rawx = ${event.rawX}]")
                    originalLeftMarginIvTrimStart =
                        (iv.layoutParams as MarginLayoutParams).leftMargin.toFloat()
                    coordinateXStart = event.rawX
                }

                MotionEvent.ACTION_MOVE -> {
                    var leftMargin = event.rawX - coordinateXStart + originalLeftMarginIvTrimStart
                    Log.d(
                        TAG,
                        "setEventViewleft: $leftMargin -- $widthScreen--- $coordinateXLimitEnd"
                    )
                    when {
                        leftMargin < 0 -> leftMargin = 0f
                        leftMargin > coordinateXLimitEnd - MIN_DISTANCE -> leftMargin =
                            coordinateXLimitEnd - MIN_DISTANCE
                    }

                    newParamIvTrimStart!!.leftMargin = leftMargin.toInt()
                    newParamTop!!.leftMargin = leftMargin.toInt()
                    newParamBottom!!.leftMargin = leftMargin.toInt()

                    iv.layoutParams = newParamIvTrimStart
                    vTop!!.layoutParams = newParamTop
                    vBottom!!.layoutParams = newParamBottom
                    changeState(true)
                }

                MotionEvent.ACTION_UP -> {
                    changeState(false)
                }
            }
            true
        }

        ivTrimEnd?.setOnTouchListener { iv, event ->
            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    originalRightMarginIvTrimEnd =
                        (iv.layoutParams as MarginLayoutParams).rightMargin.toFloat()
                    coordinateXEnd = event.rawX
                }

                MotionEvent.ACTION_MOVE -> {
                    var rightMargin = coordinateXEnd - event.rawX + originalRightMarginIvTrimEnd
                    Log.d(
                        TAG,
                        "setEventViewright: $rightMargin -- $widthScreen--- $coordinateXLimitStart"
                    )
                    when {
                        rightMargin < 0 -> rightMargin = 0f
                        rightMargin > widthScreen - coordinateXLimitStart - MIN_DISTANCE / 2 -> rightMargin =
                            widthScreen - coordinateXLimitStart - MIN_DISTANCE / 2
                    }

                    newParamIvTrimEnd!!.rightMargin = rightMargin.toInt()
                    newParamTop!!.rightMargin = rightMargin.toInt()
                    newParamBottom!!.rightMargin = rightMargin.toInt()

                    iv.layoutParams = newParamIvTrimEnd
                    vTop!!.layoutParams = newParamTop
                    vBottom!!.layoutParams = newParamBottom
                    changeState(true)
                }

                MotionEvent.ACTION_UP -> {
                    coordinateXEnd = event.rawX
                    changeState(false)
                }
            }
            true
        }
    }

    private fun changeState(hasAction: Boolean) {
        if (hasAction) {
            ivTrimStart?.setImageResource(R.drawable.ic_trim_start_action)
            ivTrimEnd?.setImageResource(R.drawable.ic_trim_start_action)
            vTop?.setBackgroundColor(getAppColor(R.color.color_orange_light))
            vBottom?.setBackgroundColor(getAppColor(R.color.color_orange_light))
        } else {
            ivTrimStart?.setImageResource(R.drawable.ic_start_point)
            ivTrimEnd?.setImageResource(R.drawable.ic_start_point)
            vTop?.setBackgroundColor(getAppColor(R.color.black))
            vBottom?.setBackgroundColor(getAppColor(R.color.black))
        }
    }

    fun extractFrameFromVideo(path: String) {
        if (threadExtractFrameVideo == null) {
            threadExtractFrameVideo = Thread {
                try {
                    val mediaMetadataRetriever = MediaMetadataRetriever()
                    mediaMetadataRetriever.setDataSource(
                        ctx,
                        Uri.parse(path)
                    )
                    // Retrieve media data use microsecond
                    val durationStr =
                        mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    val duration = durationStr!!.toLong()
                    for (i in 0 until duration step 1000) {
                        val bitmap =
                            mediaMetadataRetriever.getFrameAtTime(
                                i * 1000,
                                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                            )
                        try {
                            bitmap?.let { bitmap ->
                                listFrame.add(bitmap)
                            }
                        } catch (t: Throwable) {
                            t.printStackTrace()
                        }
                    }
                    mediaMetadataRetriever.release()
                } catch (e: Throwable) {
                    Thread.getDefaultUncaughtExceptionHandler()
                        ?.uncaughtException(Thread.currentThread(), e)
                }
                handler?.post {
                    cvFrame?.submitList(listFrame)
                }
            }
        }
        threadExtractFrameVideo!!.start()
    }
}
