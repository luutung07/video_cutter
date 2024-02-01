package com.example.videocutter.presentation.select.preview

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.baseapp.base.extension.setOnSafeClick
import com.example.library_base.extension.STRING_DEFAULT
import com.example.videocutter.R
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.PreviewFileFragmentBinding

class PreviewFileFragment :
    VideoCutterFragment<PreviewFileFragmentBinding>(R.layout.preview_file_fragment) {

    companion object {
        const val URL_PATH_KEY = "URL_PATH_KEY"
    }

    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var mediaItemIndex = 0
    private var playbackPosition = 0L
    private var urlPathVideo: String = STRING_DEFAULT

    private val playbackStateListener: Player.Listener = playbackStateListener()

    override fun onPrepareInitView() {
        super.onPrepareInitView()
        urlPathVideo = arguments?.getString(URL_PATH_KEY, STRING_DEFAULT).toString()
    }

    override fun onInitView() {
        super.onInitView()
        initializePlayer()
        setEventView()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun setEventView() {
        binding.flPreviewFileClose.setOnSafeClick {
            parentFragmentManager.popBackStack()
        }
        binding.frameLayout.post {
            Log.d(TAG, "setEventView: ${ binding.pvPreviewFile.height}")
            val newParams = binding.frameLayout.layoutParams as ViewGroup.LayoutParams
            newParams.height = binding.pvPreviewFile.height
            newParams.width = binding.pvPreviewFile.width
            binding.frameLayout.layoutParams = newParams
            Log.d(TAG, "setEventView: ${binding.frameLayout.layoutParams.height}")
        }
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(requireContext())
            .build()
            .also { exoPlayer ->
                binding.pvPreviewFile.player = exoPlayer
                val secondMediaItem = MediaItem.fromUri(urlPathVideo)
                exoPlayer.setMediaItems(
                    listOf(secondMediaItem),
                    mediaItemIndex,
                    playbackPosition
                )
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
                exoPlayer.addListener(playbackStateListener)
                exoPlayer.prepare()
            }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            mediaItemIndex = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player = null
    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d(TAG, "changed state to $stateString")
        }
    }

}
