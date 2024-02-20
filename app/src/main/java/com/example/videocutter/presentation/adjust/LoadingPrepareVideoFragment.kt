package com.example.videocutter.presentation.adjust

import com.example.baseapp.base.extension.setOnSafeClick
import com.example.library_base.eventbus.EventBusManager
import com.example.videocutter.AppConfig
import com.example.videocutter.R
import com.example.videocutter.common.event.NextEditVideoEvent
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.LoadingPrepareVideoFragmentBinding

class LoadingPrepareVideoFragment :
    VideoCutterFragment<LoadingPrepareVideoFragmentBinding>(R.layout.loading_prepare_video_fragment) {

    private var isNext = true

    override fun onInitView() {
        super.onInitView()
        binding.tvLoadingPrepareVideoStop.setOnSafeClick {
            isNext = false
            onBackPressedFragment()
        }

        binding.tvLoadingPrepareVideoStop.postDelayed({
            if (isNext) {
                EventBusManager.instance?.postPending(NextEditVideoEvent())
                onBackPressedFragment()
            }
        }, AppConfig.TIME_DELAY_EDIT_VIDEO)
    }

    override fun onBackPressedFragment() {
        parentFragmentManager.popBackStack()
    }
}
