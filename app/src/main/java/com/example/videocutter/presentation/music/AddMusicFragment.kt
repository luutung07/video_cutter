package com.example.videocutter.presentation.music

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
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

        coroutinesLaunch(viewModel.durationState) {
            handleUiState(it, object : IViewListener {

                override fun onSuccess() {
                    checkTimeLine(it.data?: LONG_DEFAULT, viewModel.idShowCropMusic)
                    viewModel.resetDuration()
                }
            })
        }
    }

    private fun setEventView() {

        if (viewModel.urlMp3 != null && exoPlayer == null) {
            initializePlayer()
        }

        handler = Handler(Looper.getMainLooper())

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
                viewModel.selectMusic(id)
                exoPlayer?.release()
                exoPlayer = null
                viewModel.urlMp3 = url
                initializePlayer()
                clearRunnable()
            }

            override fun onSelectNone(isSelect: Boolean) {
                viewModel.updateStateNone(isSelect)
                clearRunnable()
                releasePlayer()
            }

            override fun onDownloadMusic(id: String?, url: String?) {

            }

            override fun onShowCutMusic(id: String?, url: String?) {
                viewModel.showCropMedia(id)
                if (url != null && url != viewModel.urlMp3) {
                    exoPlayer?.release()
                    exoPlayer = null
                    viewModel.urlMp3 = url
                    initializePlayer()
                    clearRunnable()
                }
            }

            override fun onCropVideo(isCrop: Boolean) {
                binding.cvAddMusic.blockScroll(isCrop)
            }

            override fun onTimeLine(start: Long, end: Long, id: String?) {
                viewModel.updatePlay(id, false)
                exoPlayer?.seekTo(start)
                exoPlayer?.play()
                clearRunnable()
                checkTimeLine(end, id)
            }

            override fun onPlay(id: String?) {
                if (id == viewModel.idPlay) {
                    stopExoplayer()
                } else {
                    resumeExoplayer()
                }
                viewModel.updatePlay(id)
            }
        }
    }

    private fun removeListener() {
        adapter.listener = null
    }

    private fun checkTimeLine(end: Long, id: String?) {
        if (exoPlayer == null) return
        if (handler == null) handler = Handler(Looper.getMainLooper())

        runnable = object : Runnable {
            override fun run() {
                val current = (exoPlayer?.currentPosition ?: LONG_DEFAULT)
                if (current > end) {
                    exoPlayer?.pause()
                    clearRunnable()
                }
                viewModel.setCurrentPosition(id, current)
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
                    exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
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
