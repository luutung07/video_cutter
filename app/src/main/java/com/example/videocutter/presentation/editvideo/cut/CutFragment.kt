package com.example.videocutter.presentation.editvideo.cut

import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ConcatenatingMediaSource2
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.example.baseapp.base.extension.getAppDrawable
import com.example.baseapp.base.extension.setOnSafeClick
import com.example.library_base.extension.LONG_DEFAULT
import com.example.videocutter.AppConfig
import com.example.videocutter.R
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.CutFragmentBinding
import com.example.videocutter.presentation.widget.cut.TrimView

class CutFragment : VideoCutterFragment<CutFragmentBinding>(R.layout.cut_fragment) {

    companion object {
        const val LIST_PATH_KEY = "LIST_PATH_KEY"
        const val DURATION_KEY = "DURATION_KEY"
    }

    private var listPath: MutableList<String>? = null
    private var duration: Long? = null

    private var exoplayer: ExoPlayer? = null
    private var start: Long = LONG_DEFAULT
    private var end: Long = LONG_DEFAULT

    private var handler: Handler? = null
    private var runable: Runnable? = null

    override fun onPrepareInitView() {
        super.onPrepareInitView()
        listPath = arguments?.getStringArrayList(LIST_PATH_KEY)
        duration = arguments?.getLong(DURATION_KEY)
    }

    @UnstableApi
    override fun onInitView() {
        super.onInitView()
        setEventView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeListener()
        releasePlayer()
    }

    @UnstableApi
    private fun setEventView() {
        binding.trimCut.apply {
            setListFrame(mainViewModel.listFrameDetach.value.data ?: arrayListOf())
            setDuration(duration ?: LONG_DEFAULT)
            listener = object : TrimView.ITrimCallBack {
                override fun onTrim(start: Long, end: Long) {
                    clearRunnable()
                    this@CutFragment.start = start
                    this@CutFragment.end = end
                    updateTime()
                }
            }
        }

        binding.ivCutPlay.setOnSafeClick {
            if (exoplayer?.isPlaying == true) {
                stop()
                binding.ivCutPlay.setImageResource(R.drawable.ic_btn_pause)
            } else {
                binding.ivCutPlay.setImageResource(R.drawable.ic_btn_play)
                start()
                if (handler == null){
                    updateTime()
                }
            }
        }

        binding.hvCut.apply {
            setActionRight {
                mainViewModel.startCut = start
                mainViewModel.endCut = end
                mainViewModel.detachFrameVideo(listPath?: arrayListOf())
                onBackPressedFragment()
            }

            setActionLeft {
                mainViewModel.startCut = null
                mainViewModel.endCut = null
                onBackPressedFragment()
            }
        }

        setExoplayer()
    }

    @UnstableApi
    private fun setExoplayer() {
        if (exoplayer == null) {
            if (listPath?.isEmpty() == true) return
            val concatenatingMediaSourceBuilder = ConcatenatingMediaSource2.Builder()
            listPath?.forEach {
                val mediaSource =
                    DefaultMediaSourceFactory(requireContext()).createMediaSource(
                        MediaItem.fromUri(
                            it
                        )
                    )
                concatenatingMediaSourceBuilder.add(mediaSource, LONG_DEFAULT)
            }

            exoplayer = ExoPlayer.Builder(requireContext())
                .build()
                .also { player ->
                    binding.pvCut.player = player
                    player.setMediaSource(concatenatingMediaSourceBuilder.build())
                    player.prepare()
                    player.playWhenReady = true
                }
        }
    }

    private fun start() {
        exoplayer?.play()
    }

    private fun stop() {
        exoplayer?.pause()
    }

    private fun releasePlayer() {
        exoplayer?.release()
        exoplayer = null
        clearRunnable()
    }

    private fun updateTime() {
        if (handler == null) handler = Handler(Looper.getMainLooper())
        binding.ivCutPlay.setImageResource(R.drawable.ic_btn_play)

        exoplayer?.seekTo(start)
        exoplayer?.play()

        runable = object : Runnable {
            override fun run() {
                val current = exoplayer?.currentPosition ?: LONG_DEFAULT
                if (current >= end) {
                    stop()
                    clearRunnable()
                    exoplayer?.seekTo(start)
                    binding.ivCutPlay.setImageResource(R.drawable.ic_btn_pause)
                } else {
                    binding.trimCut.updateProgress(current, end)
                }
                handler?.postDelayed(this, AppConfig.TIME_DELAY)
            }
        }
        handler?.postDelayed(runable!!, AppConfig.TIME_DELAY)
    }

    private fun clearRunnable() {
        runable?.let {
            handler?.removeCallbacks(it)
        }
        runable = null
        handler = null
    }

    private fun removeListener() {
        binding.trimCut.listener = null
    }
}
