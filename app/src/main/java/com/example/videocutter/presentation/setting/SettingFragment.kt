package com.example.videocutter.presentation.setting

import androidx.fragment.app.viewModels
import com.example.baseapp.base.extension.getAppString
import com.example.baseapp.base.extension.setOnSafeClick
import com.example.library_base.common.usecase.IViewListener
import com.example.library_base.extension.STRING_DEFAULT
import com.example.videocutter.R
import com.example.videocutter.common.extensions.coroutinesLaunch
import com.example.videocutter.common.extensions.handleUiState
import com.example.videocutter.common.loader.aim.SlideAnimation
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.SettingFragmentBinding
import com.example.videocutter.presentation.display.model.video.QUALITY_VIDEO_TYPE
import com.example.videocutter.presentation.setting.quality.VideoQualityFragment

class SettingFragment : VideoCutterFragment<SettingFragmentBinding>(R.layout.setting_fragment) {

    private val viewModel by viewModels<SettingViewModel>()

    override fun getContainerId(): Int {
        return R.id.clSettingRoot
    }

    override fun onInitView() {
        super.onInitView()
        binding.ivSettingBack.setOnSafeClick {
            onBackPressedFragment()
        }

        binding.lrtiSettingVideoQuality.setOnSafeClick {
            replaceFragmentInsideFragment(VideoQualityFragment(), screenAnim = SlideAnimation())
        }
    }

    override fun onObserverViewModel() {
        super.onObserverViewModel()
        coroutinesLaunch(viewModel.qualityState) {
            handleUiState(it, object : IViewListener {
                override fun onSuccess() {
                    val label = when (it.data) {
                        QUALITY_VIDEO_TYPE.LOW -> getAppString(R.string.low)
                        QUALITY_VIDEO_TYPE.MEDIUM -> getAppString(R.string.medium)
                        QUALITY_VIDEO_TYPE.HIGH -> getAppString(R.string.high)
                        else -> STRING_DEFAULT
                    }
                    binding.lrtiSettingVideoQuality.setTextRight(label)
                }
            })
        }
    }
}
