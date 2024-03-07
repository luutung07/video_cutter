package com.example.videocutter.presentation.adjust

import com.example.baseapp.base.extension.setOnSafeClick
import com.example.library_base.common.coroutinesLaunch
import com.example.library_base.common.handleUiState
import com.example.library_base.common.usecase.IViewListener
import com.example.library_base.eventbus.EventBusManager
import com.example.videocutter.AppConfig
import com.example.videocutter.R
import com.example.videocutter.common.event.NextEditVideoEvent
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.LoadingPrepareVideoFragmentBinding

class LoadingPrepareVideoFragment :
    VideoCutterFragment<LoadingPrepareVideoFragmentBinding>(R.layout.loading_prepare_video_fragment) {

    override fun onInitView() {
        super.onInitView()
        binding.tvLoadingPrepareVideoStop.setOnSafeClick {
            mainViewModel.resetDetach()
            onBackPressedFragment()
        }
    }

    override fun onObserverViewModel() {
        super.onObserverViewModel()
        coroutinesLaunch(mainViewModel.listFrameDetach){
            handleUiState(it, object : IViewListener{
                override fun onSuccess() {
                    EventBusManager.instance?.postPending(NextEditVideoEvent())
                    onBackPressedFragment()
                }
            })
        }
    }

    override fun onBackPressedFragment() {
        parentFragmentManager.popBackStack()
    }
}
