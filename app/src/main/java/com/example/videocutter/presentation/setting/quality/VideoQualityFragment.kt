package com.example.videocutter.presentation.setting.quality

import androidx.fragment.app.viewModels
import com.example.baseapp.base.extension.getAppDrawable
import com.example.baseapp.base.extension.setOnSafeClick
import com.example.baseapp.base.extension.show
import com.example.library_base.common.usecase.IViewListener
import com.example.videocutter.R
import com.example.videocutter.common.extensions.coroutinesLaunch
import com.example.videocutter.common.extensions.handleUiState
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.VideoQualityFragmentBinding
import com.example.videocutter.presentation.display.model.video.QUALITY_VIDEO_TYPE
import com.example.videocutter.presentation.setting.SettingViewModel

class VideoQualityFragment :
    VideoCutterFragment<VideoQualityFragmentBinding>(R.layout.video_quality_fragment) {

    private val parentViewModel by viewModels<SettingViewModel>(
        ownerProducer = { requireParentFragment() }
    )

    override fun onInitView() {
        super.onInitView()
        binding.ivVideoQualityBack.setOnSafeClick {
            onBackPressedFragment()
        }

        binding.lrtiVideoQualityLow.setOnSafeClick {
            parentViewModel.setQuality(QUALITY_VIDEO_TYPE.LOW)
        }
        binding.lrtiVideoQualityMedium.setOnSafeClick {
            parentViewModel.setQuality(QUALITY_VIDEO_TYPE.MEDIUM)
        }
        binding.lrtiVideoQualityHigh.setOnSafeClick {
            parentViewModel.setQuality(QUALITY_VIDEO_TYPE.HIGH)
        }
    }

    override fun onObserverViewModel() {
        super.onObserverViewModel()
        coroutinesLaunch(parentViewModel.qualityState){
            handleUiState(it, object : IViewListener{
                override fun onSuccess() {
                    stateDefault()
                    when(it.data){
                        QUALITY_VIDEO_TYPE.LOW ->{
                            binding.lrtiVideoQualityLow.setIcLeft(getAppDrawable(R.drawable.ic_checkmark))
                        }

                        QUALITY_VIDEO_TYPE.MEDIUM -> {
                            binding.lrtiVideoQualityMedium.setIcLeft(getAppDrawable(R.drawable.ic_checkmark))
                        }

                        QUALITY_VIDEO_TYPE.HIGH -> {
                            binding.lrtiVideoQualityHigh.setIcLeft(getAppDrawable(R.drawable.ic_checkmark))
                        }

                        else -> stateDefault()
                    }
                }
            })
        }
    }

    override fun onBackPressedFragment() {
        parentFragmentManager.popBackStack()
    }

    private fun stateDefault() {
        binding.lrtiVideoQualityLow.showIcLeft(false)
        binding.lrtiVideoQualityMedium.showIcLeft(false)
        binding.lrtiVideoQualityHigh.showIcLeft(false)
    }
}
