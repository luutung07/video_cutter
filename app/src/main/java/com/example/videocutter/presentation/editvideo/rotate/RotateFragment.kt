package com.example.videocutter.presentation.editvideo.rotate

import com.example.baseapp.base.extension.setOnSafeClick
import com.example.videocutter.R
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.RotateFragmentBinding
import com.example.videocutter.presentation.editvideo.EditVideoFragment

class RotateFragment: VideoCutterFragment<RotateFragmentBinding>(R.layout.rotate_fragment) {

    private val parentRoot by lazy {
        parentFragment as EditVideoFragment
    }

    override fun onInitView() {
        super.onInitView()
        binding.hvRotateFooter.apply {
            setActionLeft {
                onBackPressedFragment()
            }

        }

        binding.ivRotateLeft.setOnSafeClick {
            parentRoot.rotateVideo(-90f)
        }

        binding.ivRotateRight.setOnSafeClick {
            parentRoot.rotateVideo(90f)
        }

        binding.ivRotateFlipHorizontal.setOnSafeClick {

        }

        binding.ivRotateFlipVertical.setOnSafeClick {

        }
    }

    override fun onBackPressedFragment() {
        parentRoot.showHeader(true)
        parentFragmentManager.popBackStack()
    }

}
