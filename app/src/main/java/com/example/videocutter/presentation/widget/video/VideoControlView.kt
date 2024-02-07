package com.example.videocutter.presentation.widget.video

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ConcatenatingMediaSource2
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.example.baseapp.base.extension.getAppDrawable
import com.example.baseapp.base.extension.getAppString
import com.example.library_base.extension.INT_DEFAULT
import com.example.library_base.extension.LONG_DEFAULT
import com.example.videocutter.AppConfig
import com.example.videocutter.R
import com.example.videocutter.common.extensions.convertTimeToString
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

    private var handler: Handler? = null
    private var runable: Runnable? = null

    var listenerStateVideo: IVideoControlCallback.IStateVideo? = null

    init {
        LayoutInflater.from(ctx).inflate(R.layout.video_layout, this, true)
        initView(attrs)
        handler = Handler(Looper.getMainLooper())
    }

    private fun initView(attrs: AttributeSet?) {
        playView = findViewById(R.id.pvVideo)
        timeLine = findViewById(R.id.tlvVideo)
        timeLine?.setTvStart(getAppString(R.string.time_start))
    }

    private fun setUpView() {
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

                        listenerStateVideo?.onVideoEnd()
                    }
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                Log.d(TAG, "onMediaItemTransition: ")
                listenerStateVideo?.onVideoTransaction(mediaItem, reason)
            }

            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)
                Log.d(TAG, "onEvents: $events")
                if (
                    events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED) ||
                    events.contains(Player.EVENT_PLAY_WHEN_READY_CHANGED)
                ) {
                    Log.d(TAG, "onEvents: updateTimeLine")
                    updateTimeLine()
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

    @UnstableApi
    fun setListPath(list: List<String>) {
        listPath = list
        setExoplayer()
    }

    @UnstableApi
    fun swapListPath(oldIndex: Int, newIndex: Int) {
        Collections.swap(listPath, oldIndex, newIndex)
        releasePlayer()
        setListPath(listPath)
        handler = Handler(Looper.myLooper()!!)
        updateTimeLine()
    }

    @UnstableApi
    fun deletePath(path: String) {
        val newList = listPath.toMutableList()
        newList.remove(path)
        listPath = newList

        releasePlayer()
        setListPath(listPath)
        handler = Handler(Looper.myLooper()!!)
        updateTimeLine()
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
                }
        }
    }

    fun setTimeLimeMax(max: Long) {
        timeLine?.setImageDrawableIcLeft(getAppDrawable(R.drawable.ic_btn_play))
        timeLine?.setTvEnd(max.convertTimeToString())
        timeLine?.setMaxProgress(max.toInt())
    }

    fun start() {
        exoplayer?.play()
        if (handler == null) {
            handler = Handler(Looper.getMainLooper())
        }
        setUpView()
    }

    fun stop() {
        exoplayer?.pause()
        timeLine?.setImageDrawableIcLeft(getAppDrawable(R.drawable.ic_btn_pause))
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

    interface IVideoControlCallback {

        interface IStateVideo {
            fun onInitVideo() {}
            fun onPendingVideo() {}
            fun onInitVideoSuccess() {}
            fun onVideoEnd() {}
            fun onVideoError(exception: Exception) {}
            fun onVideoTransaction(
                mediaItem: MediaItem?,
                @Player.MediaItemTransitionReason reason: Int
            ) {
            }
        }
    }
}
