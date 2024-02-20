package com.example.videocutter.presentation.editvideo

import androidx.fragment.app.viewModels
import androidx.media3.common.util.UnstableApi
import com.example.videocutter.R
import com.example.videocutter.common.extensions.convertTimeToString
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.EditVideoFragmentBinding
import com.example.videocutter.presentation.widget.video.VideoControlView

class EditVideoFragment :
    VideoCutterFragment<EditVideoFragmentBinding>(R.layout.edit_video_fragment) {

    companion object {
        const val VIDEO_INFO_EDIT_KEY = "VIDEO_INFO_EDIT_KEY"
    }

    private val viewModel by viewModels<EditVideoViewModel>()

    private var isPlay = true

    @UnstableApi
    override fun onInitView() {
        super.onInitView()
        setUpView()
    }

    override fun onStop() {
        super.onStop()
        binding.vcvEditVideo.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.vcvEditVideo.releasePlayer()
    }

    @UnstableApi
    private fun setUpView() {
        binding.hvEditVideo.apply {
            setActionLeft {
                onBackPressedFragment()
            }
        }

        binding.vcvEditVideo.apply {
            setTimeCenter(viewModel.maxDuration.convertTimeToString())
            setListPath(viewModel.listPath, hasExtract = true, hasTimeStart = false)
            hasTimeStart(false)
            listenerStateVideo = object : VideoControlView.IVideoControlCallback.IStateVideo {
                override fun onVideoEnd() {
                    isPlay = false
                }
            }
            setOnLeftListener {
                isPlay = !isPlay
                if (isPlay) {
                    start()
                } else {
                    stop()
                }
            }
        }
    }
}
