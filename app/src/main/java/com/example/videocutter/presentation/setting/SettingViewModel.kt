package com.example.videocutter.presentation.setting

import androidx.lifecycle.ViewModel
import com.example.library_base.common.success
import com.example.library_base.common.usecase.FlowResult
import com.example.videocutter.presentation.AppPreferences
import com.example.videocutter.presentation.display.model.video.QUALITY_VIDEO_TYPE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingViewModel : ViewModel() {

    private var _qualityState = MutableStateFlow(FlowResult.newInstance<QUALITY_VIDEO_TYPE>())
    val qualityState = _qualityState.asStateFlow()

    init {
        getQuality()
    }

    private fun getQuality() {
        val type = QUALITY_VIDEO_TYPE.getItem(AppPreferences.qualityValueVideo)
        type?.let {
            setQuality(type)
        }
    }

    fun setQuality(qualityVideoType: QUALITY_VIDEO_TYPE) {
        AppPreferences.qualityValueVideo = qualityVideoType.value
        _qualityState.success(qualityVideoType)
    }

}
