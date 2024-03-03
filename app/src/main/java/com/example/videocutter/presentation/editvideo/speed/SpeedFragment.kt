package com.example.videocutter.presentation.editvideo.speed

import androidx.fragment.app.viewModels
import com.example.videocutter.R
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.SpeedFragmentBinding
import com.example.videocutter.presentation.editvideo.EditVideoFragment
import com.example.videocutter.presentation.editvideo.EditVideoViewModel
import com.example.videocutter.presentation.widget.speedvideo.SPEED_TYPE
import com.example.videocutter.presentation.widget.speedvideo.SpeedVideoView

class SpeedFragment : VideoCutterFragment<SpeedFragmentBinding>(R.layout.speed_fragment) {

    private val viewModel by viewModels<EditVideoViewModel>(ownerProducer = {
        requireParentFragment()
    })

    private val parentRoot by lazy {
        parentFragment as EditVideoFragment
    }

    override fun onInitView() {
        super.onInitView()
        binding.hvSpeedFooter.apply {
            setActionLeft {
                viewModel.speedVideo = SPEED_TYPE.SPEED_1
                parentRoot.setSpeed(viewModel.speedVideo)
                onBackPressedFragment()
            }

            setActionRight {
                onBackPressedFragment()
            }
        }
        binding.speedVideo.apply {

            setOldPosition(viewModel.speedVideo)
            parentRoot.setSpeed(viewModel.speedVideo)

            listener = object : SpeedVideoView.ISpeedListener {
                override fun onSpeedChange(type: SPEED_TYPE) {
                    viewModel.speedVideo = type
                    parentRoot.setSpeed(viewModel.speedVideo)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeListener()
    }

    override fun onBackPressedFragment() {
        parentRoot.showHeader(true)
        parentFragmentManager.popBackStack()
    }

    private fun removeListener() {
        binding.speedVideo.listener = null
    }
}
