package com.example.videocutter.presentation.editvideo

import com.example.videocutter.R
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.EditVideoFragmentBinding

class EditVideoFragment :
    VideoCutterFragment<EditVideoFragmentBinding>(R.layout.edit_video_fragment) {

    override fun onInitView() {
        super.onInitView()
        setUpView()
    }

    private fun setUpView() {
        binding.hvEditVideo.apply {

        }
    }
}
