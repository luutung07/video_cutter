package com.example.videocutter.presentation.widget.video

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ConcatenatingMediaSource2
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.chibde.visualizer.LineBarVisualizer
import com.example.baseapp.base.extension.getAppDrawable
import com.example.baseapp.base.extension.getAppString
import com.example.baseapp.base.extension.gone
import com.example.baseapp.base.extension.show
import com.example.library_base.extension.INT_DEFAULT
import com.example.library_base.extension.LONG_DEFAULT
import com.example.videocutter.AppConfig
import com.example.videocutter.R
import com.example.videocutter.common.extensions.convertTimeToString
import com.example.videocutter.presentation.widget.recyclerview.COLLECTION_MODE
import com.example.videocutter.presentation.widget.recyclerview.CollectionView
import com.example.videocutter.presentation.widget.timeline.TimelineView
import java.util.Collections

class VideoControlView constructor(
    private val ctx: Context,
    attrs: AttributeSet?
) : ConstraintLayout(ctx, attrs) {

    companion object {
        private const val TAG = "VideoControlView"
    }

    private var timeLine: TimelineView? = null
    private var timeEnd: Long? = null

    private var playView: PlayerView? = null
    private var exoplayer: ExoPlayer? = null
    private var listPath: List<String> = arrayListOf()

    private var clExtractRoot: ConstraintLayout? = null
    private var cvExtract: CollectionView? = null
    private var lineBarVisualizer: LineBarVisualizer? = null

    private var handler: Handler? = null
    private var runable: Runnable? = null
    private var runnableScrollExtract: Runnable? = null
    private val listFrame = LinkedHashSet<Bitmap>()
    private var hasExtract = false

    private val extractAdapter by lazy { ExtractVideoAdapter() }

    var listenerStateVideo: IVideoControlCallback.IStateVideo? = null

    init {
        LayoutInflater.from(ctx).inflate(R.layout.video_layout, this, true)
        initView(attrs)
        handler = Handler(Looper.getMainLooper())
    }

    private fun initView(attrs: AttributeSet?) {
        playView = findViewById(R.id.pvVideo)
        timeLine = findViewById(R.id.tlvVideo)
        clExtractRoot = findViewById(R.id.clVideoExtract)
        cvExtract = findViewById(R.id.cvVideoExtract)
        lineBarVisualizer = findViewById(R.id.lineBarVisualizerVideoExtract)

        setUpAdapter()
    }

    private fun setUpView() {

        lineBarVisualizer?.setColor(ContextCompat.getColor(ctx, R.color.white));
        lineBarVisualizer?.setDensity(70f);

        exoplayer?.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Log.d(TAG, "onPlayerError: $error")
                listenerStateVideo?.onVideoError(error)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        Log.d(TAG, "onPlaybackStateChanged: STATE_BUFFERING")
                        listenerStateVideo?.onInitVideo()
                    }

                    Player.STATE_IDLE -> {
                        Log.d(TAG, "onPlaybackStateChanged: STATE_IDLE")
                        listenerStateVideo?.onPendingVideo()
                    }

                    Player.STATE_READY -> {
                        Log.d(TAG, "onPlaybackStateChanged: STATE_READY")
                        if (!hasExtract) {
                            updateTimeLine()
                        } else {
                            updateScrollExtractVideo()
                        }
                        listenerStateVideo?.onInitVideoSuccess()
                    }

                    Player.STATE_ENDED -> {
                        Log.d(TAG, "onPlaybackStateChanged: STATE_ENDED")
                        timeLine?.apply {
                            setImageDrawableIcLeft(getAppDrawable(R.drawable.ic_btn_pause))
                            setTvStart(getAppString(R.string.time_start))
                            setProgressSeekbar(INT_DEFAULT)
                        }
                        exoplayer?.seekTo(LONG_DEFAULT)
                        exoplayer?.pause()

                        releaseRunableExtract()
                        listenerStateVideo?.onVideoEnd()
                    }
                }
            }
        })

        timeLine?.listener = object : TimelineView.ITimeLineCallBack {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    exoplayer?.seekTo(progress.toLong())
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                super.onStopTrackingTouch(seekBar)
                exoplayer?.play()
                timeLine?.setImageDrawableIcLeft(getAppDrawable(R.drawable.ic_btn_play))
            }
        }
    }

    private fun updateTimeLine() {
        runable = object : Runnable {
            override fun run() {
                val time = exoplayer?.currentPosition ?: LONG_DEFAULT
                timeLine?.setProgressSeekbar(time.toInt())
                timeLine?.setTvStart(time.convertTimeToString())
                handler?.postDelayed(this, AppConfig.TIME_DELAY_PLAY_VIDEO)
            }
        }
        handler?.postDelayed(runable!!, AppConfig.TIME_DELAY_PLAY_VIDEO)
    }

    private fun updateScrollExtractVideo() {
        runnableScrollExtract = object : Runnable {
            override fun run() {
                val index = ((exoplayer?.currentPosition ?: 1) * (cvExtract?.width
                    ?: 1)) / (exoplayer?.duration ?: 1)
                cvExtract!!.smoothScrollBy(index.toInt(), 0)
                handler?.postDelayed(this, AppConfig.TIME_DELAY_PLAY_VIDEO)
            }
        }
        handler?.postDelayed(runnableScrollExtract!!, AppConfig.TIME_DELAY_PLAY_VIDEO)
    }

    private fun setUpAdapter() {
        cvExtract?.setAdapter(extractAdapter)
        cvExtract?.setLayoutManager(COLLECTION_MODE.HORIZONTAL)
    }

    @UnstableApi
    private fun extractFrameFromVideo() {
        Thread {
            try {
                val mediaMetadataRetriever = MediaMetadataRetriever()
                listPath.forEach {
                    mediaMetadataRetriever.setDataSource(
                        ctx,
                        Uri.parse(it)
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
                            bitmap?.let { bitmap -> listFrame.add(bitmap) }
                        } catch (t: Throwable) {
                            t.printStackTrace()
                        }
                    }
                }
                mediaMetadataRetriever.release()
            } catch (e: Throwable) {
                Thread.getDefaultUncaughtExceptionHandler()
                    .uncaughtException(Thread.currentThread(), e)
            }
            handler?.post {
                clExtractRoot?.show()
                cvExtract?.submitList(listFrame.toList())
                setExoplayer()
            }
        }.start()
    }

    @UnstableApi
    fun setListPath(list: List<String>, hasExtract: Boolean = false, hasTimeStart: Boolean = true) {
        listPath = list
        this.hasExtract = hasExtract
        if (hasExtract) {
            extractFrameFromVideo()
        } else {
            setExoplayer()
        }

        if (hasTimeStart) {
            timeLine?.setTvStart(getAppString(R.string.time_start))
        }
    }

    fun hasTimeStart(hasTimeStart: Boolean) {
        timeLine?.hasTimeStart(hasTimeStart)
    }


    @UnstableApi
    fun swapListPath(oldIndex: Int, newIndex: Int) {
        Collections.swap(listPath, oldIndex, newIndex)
        releasePlayer()
        setListPath(listPath)
        handler = Handler(Looper.myLooper()!!)
    }

    @UnstableApi
    fun deletePath(path: String) {
        val newList = listPath.toMutableList()
        newList.remove(path)
        listPath = newList

        releasePlayer()
        setListPath(listPath)
        handler = Handler(Looper.myLooper()!!)
    }

    fun setOnLeftListener(action: () -> Unit) {
        timeLine?.setOnActionLeft(action)
    }

    @UnstableApi
    fun setExoplayer() {
        if (exoplayer == null) {
            if (listPath.isEmpty()) return
            val concatenatingMediaSourceBuilder = ConcatenatingMediaSource2.Builder()
            listPath.forEach {
                val mediaSource =
                    DefaultMediaSourceFactory(ctx).createMediaSource(MediaItem.fromUri(it))
                concatenatingMediaSourceBuilder.add(mediaSource, LONG_DEFAULT)
            }

            exoplayer = ExoPlayer.Builder(ctx)
                .build()
                .also { player ->
                    playView?.player = player
                    player.setMediaSource(concatenatingMediaSourceBuilder.build())
                    player.prepare()
                    player.playWhenReady = true
                    lineBarVisualizer?.setPlayer(player.audioSessionId)
                    timeLine?.setImageDrawableIcLeft(getAppDrawable(R.drawable.ic_btn_play))
                }
        }
        setUpView()
    }

    fun setTimeLimeMax(max: Long) {
        timeLine?.setTvEnd(max.convertTimeToString())
        timeLine?.setMaxProgress(max.toInt())
    }

    fun start() {
        exoplayer?.play()
        if (handler == null) {
            handler = Handler(Looper.getMainLooper())
        }
        timeLine?.setImageDrawableIcLeft(getAppDrawable(R.drawable.ic_btn_play))
    }

    fun stop() {
        exoplayer?.pause()
        timeLine?.setImageDrawableIcLeft(getAppDrawable(R.drawable.ic_btn_pause))
    }

    fun releaseRunableExtract() {
        runnableScrollExtract?.let {
            handler?.removeCallbacks(it)
        }
        handler?.post {
            cvExtract?.scrollToPosition(0)
        }
        runnableScrollExtract = null
    }

    fun releasePlayer() {
        exoplayer?.release()
        exoplayer = null
        runable?.let {
            handler?.removeCallbacks(it)
        }

        runable = null
        handler = null
    }

    fun setTimeEnd(duration: Long) {
        timeEnd = duration
        timeLine?.setMaxProgress(duration.toInt())
    }

    fun setTimeCenter(time: String) {
        timeLine?.setTvCenter(time)
    }

    fun setImageDrawableIcRight(drawable: Drawable) {
        timeLine?.setImageDrawableIcRight(drawable)
    }

    fun setOnActionRight(action: () -> Unit) {
        timeLine?.setOnActionRight(action)
    }

    interface IVideoControlCallback {

        interface IStateVideo {
            fun onInitVideo() {}
            fun onPendingVideo() {}
            fun onInitVideoSuccess() {}
            fun onVideoEnd() {}
            fun onVideoError(exception: Exception) {}
        }
    }
}
