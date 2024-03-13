package com.example.videocutter.presentation.widget.cut

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ConcatenatingMediaSource2
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baseapp.base.extension.getAppColor
import com.example.baseapp.base.extension.getAppDimension
import com.example.library_base.extension.LONG_DEFAULT
import com.example.videocutter.AppConfig
import com.example.videocutter.R
import com.example.videocutter.common.extensions.getCoordinateXView
import com.example.videocutter.common.extensions.getCoordinateYView
import com.example.videocutter.presentation.repodisplay.model.editvideo.DetachFrameDisplay
import com.example.videocutter.presentation.widget.recyclerview.COLLECTION_MODE
import com.example.videocutter.presentation.widget.recyclerview.CollectionView
import com.example.videocutter.presentation.widget.video.ExtractVideoAdapter

class TrimVideo constructor(
    private val ctx: Context,
    attrs: AttributeSet?
) : ConstraintLayout(ctx, attrs) {

    companion object {
        private const val TAG = "TrimVideo"
        private const val MIN_DISTANCE = 100f
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
    private var coordinateYTop: Float? = null

    private var vBottom: View? = null
    private var newParamBottom: MarginLayoutParams? = null

    private var ivTimeline: ImageView? = null
    private var newParamTimeLine: MarginLayoutParams? = null

    private var cvFrame: CollectionView? = null

    private var clTrim: ConstraintLayout? = null
    private var pvVideo: PlayerView? = null
    private var exoPlayer: ExoPlayer? = null
    private var listPath: List<String>? = null

    private var ivPlay: ImageView? = null

    private var widthScreen: Float = 0f
    private var heightScreen: Float = 0f
    private var coordinateXLimitEnd = 0f
    private var coordinateXLimitStart = 0f

    private val paintOverlay = Paint().apply {
        color = getAppColor(R.color.over_lay)
    }

    private val listFrame: MutableList<DetachFrameDisplay> = arrayListOf()
    private val extractAdapter by lazy { ExtractVideoAdapter() }

    private var totalItemCount: Int? = null
    private var firstVisibleItemPosition: Int? = null
    private var lastVisibleItemIndex: Int? = null
    private var visibleItemCount: Int? = null
    private var indexStartRv: Float? = null
    private var indexEndRv: Float? = null

    private var trimDurationStart: Long? = null
    private var trimDurationEnd: Long? = null

    private var threadExtractFrameVideo: Thread? = null

    var listener: ITrimVideoCallback? = null

    private var handler: Handler? = null
    private var runable: Runnable? = null

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
        coordinateXLimitEnd =
            ivTrimEnd!!.getCoordinateXView().toFloat() - ivTrimEnd!!.width
        coordinateYTop =
            vTop!!.getCoordinateYView()
                .toFloat() - clTrim!!.height / 2 - ivTrimEnd!!.width - vTop!!.height / 3
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        drawOutline(canvas)
    }

    private fun drawOutline(canvas: Canvas) {
        Log.d(
            TAG,
            "drawOutline: " +
                    "[newParamIvTrimStart!!.leftMargin = ${newParamIvTrimStart!!.leftMargin} -- " +
                    "[width = $widthScreen]-- [height = $heightScreen] -- " +
                    "[newParamIvTrimEnd!!.rightMargin = ${newParamIvTrimEnd!!.rightMargin}] -- " +
                    "[coordinateYTop = ${coordinateYTop}] --" +
                    "[height = ${heightScreen - clTrim!!.height}]"
        )
        canvas.drawRect(
            getAppDimension(com.example.library_base.R.dimen.dimen_20),
            coordinateYTop!!,
            newParamIvTrimStart!!.leftMargin.toFloat() + getAppDimension(com.example.library_base.R.dimen.dimen_20),
            heightScreen,
            paintOverlay
        )
        canvas.drawRect(
            widthScreen - newParamIvTrimEnd!!.rightMargin - getAppDimension(com.example.library_base.R.dimen.dimen_20),
            coordinateYTop!!,
            widthScreen - getAppDimension(com.example.library_base.R.dimen.dimen_20),
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
        clTrim = findViewById(R.id.clTrim)
        pvVideo = findViewById(R.id.pvTrim)
        ivPlay = findViewById(R.id.ivTrimPlay)

        newParamIvTrimStart = ivTrimStart!!.layoutParams as MarginLayoutParams
        newParamIvTrimEnd = ivTrimEnd!!.layoutParams as MarginLayoutParams
        newParamTop = vTop!!.layoutParams as MarginLayoutParams
        newParamBottom = vBottom!!.layoutParams as MarginLayoutParams
        newParamTimeLine = ivTimeline!!.layoutParams as MarginLayoutParams?

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

                    // end
                    val rawXEnd = (ivTrimEnd!!.layoutParams as MarginLayoutParams).rightMargin
                    indexEndRv =
                        lastVisibleItemIndex!! - ((rawXEnd * lastVisibleItemIndex!!) / (widthScreen))

                    trimDurationEnd =
                        ((indexEndRv!! * exoPlayer!!.duration) / totalItemCount!!).toLong()
                    Log.d(
                        TAG,
                        "[indexEnd = $indexEndRv] -- [total = $totalItemCount] -- [position = $trimDurationEnd] -- [duration = ${exoPlayer!!.duration}]"
                    )

                    // start
                    val rawX = event.rawX
                    val distance = (lastVisibleItemIndex!! - firstVisibleItemPosition!!)
                    indexStartRv = ((rawX * distance) / (widthScreen)) + firstVisibleItemPosition!!
                    trimDurationStart =
                        ((indexStartRv!! * exoPlayer!!.duration) / totalItemCount!!).toLong()
                    Log.d(
                        TAG, "[indexStartRv = $indexStartRv] -- " +
                                "[total = $totalItemCount] -- " +
                                "[position = $trimDurationStart] -- " +
                                "[duration = ${exoPlayer!!.duration}]--" +
                                "[firstVisibleItemPosition = $firstVisibleItemPosition]"
                    )

                    exoPlayer!!.seekTo(trimDurationStart!!)
                    exoPlayer!!.play()
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
                    Log.d(TAG, "setEventView: y= ${event.rawX}")
                    var rightMargin = coordinateXEnd - event.rawX + originalRightMarginIvTrimEnd
                    Log.d(
                        TAG,
                        "setEventViewright: $rightMargin -- $widthScreen--- $coordinateXLimitStart -- ${widthScreen - coordinateXLimitStart - MIN_DISTANCE - 3f * ivTrimStart!!.width}"
                    )
                    when {
                        rightMargin < 0 -> rightMargin = 0f
                        rightMargin > widthScreen - coordinateXLimitStart - MIN_DISTANCE - 3f * ivTrimStart!!.width -> rightMargin =
                            widthScreen - coordinateXLimitStart - MIN_DISTANCE - 3f * ivTrimStart!!.width
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
                    changeState(false)

                    // start
                    val rawX = (ivTrimStart!!.layoutParams as MarginLayoutParams).leftMargin
                    val distance = (lastVisibleItemIndex!! - firstVisibleItemPosition!!)
                    indexStartRv = ((rawX * distance) / (widthScreen)) + firstVisibleItemPosition!!
                    trimDurationStart =
                        ((indexStartRv!! * exoPlayer!!.duration) / totalItemCount!!).toLong()

                    Log.d(
                        TAG, "[indexStartRv = $indexStartRv] -- " +
                                "[total = $totalItemCount] -- " +
                                "[position = $trimDurationStart] -- " +
                                "[duration = ${exoPlayer!!.duration}]--" +
                                "[firstVisibleItemPosition = $firstVisibleItemPosition] --" +
                                "[lastVisibleItemIndex = $lastVisibleItemIndex]"
                    )

                    // end
                    coordinateXEnd = event.rawX
                    val rawXEnd = widthScreen - coordinateXEnd
                    indexEndRv =
                        ((coordinateXEnd * distance) / (widthScreen)) + firstVisibleItemPosition!!

                    trimDurationEnd =
                        ((indexEndRv!! * exoPlayer!!.duration) / totalItemCount!!).toLong()

                    Log.d(
                        TAG, "[indexEndRv = $indexEndRv] -- " +
                                "[total = $totalItemCount] -- " +
                                "[position = $trimDurationEnd] -- " +
                                "[duration = ${exoPlayer!!.duration}]--" +
                                "[firstVisibleItemPosition = $firstVisibleItemPosition] --" +
                                "[lastVisibleItemIndex = $lastVisibleItemIndex] --" +
                                "[rawX = $coordinateXEnd]--" +
                                "[widthScreen = $widthScreen]"
                    )

                    exoPlayer!!.seekTo(trimDurationStart!!)
                    exoPlayer!!.play()
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

    @UnstableApi
    private fun setExoplayer() {
        if (exoPlayer == null) {
            if (listPath?.isEmpty() == true) return
            val concatenatingMediaSourceBuilder = ConcatenatingMediaSource2.Builder()
            listPath?.forEach {
                val mediaSource =
                    DefaultMediaSourceFactory(ctx).createMediaSource(MediaItem.fromUri(it))
                concatenatingMediaSourceBuilder.add(mediaSource, LONG_DEFAULT)
            }

            exoPlayer = ExoPlayer.Builder(ctx)
                .build()
                .also { player ->
                    pvVideo?.player = player
                    player.setMediaSource(concatenatingMediaSourceBuilder.build())
                    player.prepare()
                    player.playWhenReady = true
                }
        }

        exoPlayer!!.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying) {
                    ivPlay?.setImageResource(R.drawable.ic_btn_play)
                    updateTimeLine()
                } else {

                    ivPlay?.setImageResource(R.drawable.ic_btn_pause)
                    runable?.let {
                        handler?.removeCallbacks(it)
                    }
                    runable = null
                    handler = null

                    // reset time line
                    newParamTimeLine?.leftMargin = 0
                    ivTimeline!!.layoutParams = newParamTimeLine
                }
            }
        })
    }

    private fun updateTimeLine() {
        handler = Handler(Looper.myLooper()!!)
        runable = object : Runnable {
            override fun run() {
                val duration = exoPlayer?.currentPosition ?: LONG_DEFAULT
                if (trimDurationStart == null || trimDurationEnd == null) return
                if (duration < trimDurationStart!! || duration > trimDurationEnd!!) {
                    exoPlayer!!.pause()
                } else {
                    val position = widthScreen * exoPlayer!!.currentPosition / trimDurationEnd!!
                    if (position < (widthScreen - (ivTrimEnd!!.layoutParams as MarginLayoutParams).rightMargin) - 2 * getAppDimension(
                            com.example.library_base.R.dimen.dimen_20
                        ) - ivTrimEnd!!.width
                    ) {
                        newParamTimeLine?.leftMargin = position.toInt()
                        ivTimeline!!.layoutParams = newParamTimeLine
                    }
                    Log.d(
                        TAG, "run: " +
                                "[position = $position] -- " +
                                "[current = ${exoPlayer!!.currentPosition}] -- " +
                                "[trimDurationEnd = $trimDurationEnd] -- " +
                                "[trimDurationStart = $trimDurationStart] --"
                    )
                }
                handler?.postDelayed(this, AppConfig.TIME_DELAY_RUN_VIDEO)
            }
        }
        handler?.postDelayed(runable!!, AppConfig.TIME_DELAY_PLAY_VIDEO)
    }

    @UnstableApi
    fun setListPath(list: List<String>) {
        listPath = list
        setExoplayer()

        cvFrame?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                visibleItemCount =
                    (recyclerView.layoutManager as? LinearLayoutManager)?.getChildCount()
                totalItemCount =
                    (recyclerView.layoutManager as? LinearLayoutManager)?.getItemCount()
                firstVisibleItemPosition =
                    (recyclerView.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
                lastVisibleItemIndex =
                    (recyclerView.layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition()
                val seekToPosition =
                    (firstVisibleItemPosition!! * exoPlayer!!.duration) / totalItemCount!!

                Log.d(
                    TAG,
                    "[firstVisibleItemPosition = $firstVisibleItemPosition] -- [lastVisibleItemIndex = $lastVisibleItemIndex] -- [totalItemCount = $totalItemCount]"
                )

                exoPlayer!!.seekTo(seekToPosition)
                exoPlayer!!.pause()

                listener?.onPositionRunning(firstVisibleItemPosition, totalItemCount)
            }
        })
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
                                i * 2000,
                                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                            )
                        try {
                            bitmap?.let { bitmap ->
                                listFrame.add(DetachFrameDisplay(bitmap, i))
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

    interface ITrimVideoCallback {
        fun onFindIndex(rawX: Float, widthScreen: Float)
        fun onPositionRunning(currentIndex: Int?, total: Int?)
    }
}
