package com.example.videocutter.presentation.music

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.baseapp.base.extension.setOnSafeClick
import com.example.library_base.common.usecase.IViewListener
import com.example.library_base.extension.LONG_DEFAULT
import com.example.videocutter.AppConfig
import com.example.videocutter.R
import com.example.videocutter.common.extensions.coroutinesLaunch
import com.example.videocutter.common.extensions.handleUiState
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.AddMusicFragmentBinding
import com.example.videocutter.presentation.display.model.music.MUSIC_TYPE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMusicFragment : VideoCutterFragment<AddMusicFragmentBinding>(R.layout.add_music_fragment) {

    private val viewModel by viewModels<AddMusicViewModel>()

    private val adapter by lazy { AddMusicAdapter() }

    private var exoPlayer: ExoPlayer? = null

    private var handler: Handler? = null
    private var runnable: Runnable? = null

    override fun onInitView() {
        super.onInitView()
        setEventView()
        setUpAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
        removeListener()
        clearRunnable()
    }

    override fun onObserverViewModel() {
        super.onObserverViewModel()
        coroutinesLaunch(viewModel.musicListState) {
            handleUiState(it, object : IViewListener {

                override fun onLoading() {
                    super.onLoading()
                    showLoading()
                }

                override fun onFailure() {
                    super.onFailure()
                    hideLoading()
                }

                override fun onSuccess() {
                    hideLoading()
                    binding.cvAddMusic.submitList(it.data)
                }
            })
        }

        coroutinesLaunch(viewModel.timeLineState) {
            handleUiState(it, object : IViewListener {

                override fun onSuccess() {
                    if (it.data == null) return
                    exoPlayer?.seekTo(it.data!!.first)
                    checkTimeLine(
                        it.data?.first ?: LONG_DEFAULT,
                        it.data?.second ?: LONG_DEFAULT,
                        viewModel.idShowCropMusic
                    )
                    viewModel.resetTimeline()
                }
            })
        }
    }

    private fun setEventView() {

        if (viewModel.urlMp3 != null && exoPlayer == null) {
            initializePlayer()
        }

        binding.ivAddMusicBack.setOnSafeClick {
            onBackPressedFragment()
        }

        binding.tsAddMusic.setOnActionLeft {
            viewModel.musicType = MUSIC_TYPE.ITUNES
            viewModel.getListMusic(true)
        }

        binding.tsAddMusic.setOnActionRight {
            viewModel.musicType = MUSIC_TYPE.LOCAL
            viewModel.getListMusic(true)
        }
    }

    private fun setUpAdapter() {
        binding.cvAddMusic.setAdapter(adapter)
        addListener()
    }

    private fun addListener() {
        adapter.listener = object : AddMusicAdapter.IMusicCallBack {
            override fun onSelectMusic(id: String?, url: String?) {
                releasePlayer()
                viewModel.urlMp3 = url
                initializePlayer()
                clearRunnable()
                viewModel.selectMusic(id)
            }

            override fun onSelectNone(isSelect: Boolean) {
                clearRunnable()
                releasePlayer()
                viewModel.updateStateNone(isSelect)
            }

            override fun onDownloadMusic(id: String?, url: String?) {

            }

            override fun onShowCutMusic(id: String?, url: String?, start: Long, end: Long) {
                viewModel.showCropMedia(id)
                if (url != null && url != viewModel.urlMp3) {
                    releasePlayer()
                    viewModel.urlMp3 = url
                    initializePlayer()
                    checkTimeLine(start, end, id)
                }
                exoPlayer?.seekTo(start)
                exoPlayer?.play()
            }

            override fun onCropVideo(isCrop: Boolean) {
                binding.cvAddMusic.blockScroll(isCrop)
            }

            override fun onTimeLine(start: Long, end: Long, id: String?) {
                viewModel.updatePlay(id, false)
                exoPlayer?.seekTo(start)
                exoPlayer?.play()
                checkTimeLine(start, end, id)
            }

            override fun onPlay(id: String?, isReset: Boolean, start: Long?, end: Long?) {
                if (isReset) {
                    exoPlayer?.seekTo(start ?: 0)
                    exoPlayer?.play()
                    viewModel.updatePlay(id, isChangeState = false)
                    checkTimeLine(start ?: 0, end ?: 0, id)
                } else {
                    if (viewModel.isPlaying(id)) {
                        stopExoplayer()
                    } else {
                        resumeExoplayer()
                    }
                    viewModel.updatePlay(id)
                }
            }

            override fun onDoneCrop(id: String?) {
                viewModel.doneCrop(id)
            }
        }
    }

    private fun removeListener() {
        adapter.listener = null
    }

    private fun checkTimeLine(start: Long, end: Long, id: String?) {
        if (exoPlayer == null) return
        clearRunnable()

        handler = Handler(Looper.getMainLooper())

        runnable = object : Runnable {
            override fun run() {
                val current = (exoPlayer?.currentPosition ?: LONG_DEFAULT)
                if (current >= end) {
                    exoPlayer?.pause()
                    exoPlayer?.seekTo(start)
                    viewModel.updatePlay(id, isChangeState = false, isPlay = false)
                    clearRunnable()
                }
                viewModel.setCurrentPosition(id, current, start, end)
                handler?.postDelayed(this, AppConfig.TIME_DELAY)
            }
        }
        handler?.postDelayed(runnable!!, AppConfig.TIME_DELAY)
    }

    private fun initializePlayer() {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(requireContext())
                .build()
                .also { exoPlayer ->
                    val secondMediaItem = MediaItem.fromUri(viewModel.urlMp3.toString())
                    exoPlayer.setMediaItems(
                        listOf(secondMediaItem),
                    )
                    exoPlayer.playWhenReady = true
                    exoPlayer.prepare()
                }
        }
    }

    private fun resumeExoplayer() {
        exoPlayer?.play()
    }

    private fun stopExoplayer() {
        exoPlayer?.pause()
    }

    private fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    private fun clearRunnable() {
        runnable?.let {
            handler?.removeCallbacks(it)
        }
        runnable = null
        handler = null
    }

}
