package com.example.videocutter.presentation.editvideo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.library_base.common.success
import com.example.library_base.common.usecase.FlowResult
import com.example.library_base.extension.LONG_DEFAULT
import com.example.videocutter.domain.model.VideoInfo
import com.example.videocutter.presentation.display.IRepoDisplay
import com.example.videocutter.presentation.display.model.editvideo.CropDisplay
import com.example.videocutter.presentation.display.model.editvideo.FILTER_TYPE
import com.example.videocutter.presentation.display.model.editvideo.FeatureEditVideoDisplay
import com.example.videocutter.presentation.display.model.editvideo.FilterDisplay
import com.example.videocutter.presentation.widget.crop.CROP_TYPE
import com.example.videocutter.presentation.widget.speedvideo.SPEED_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditVideoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repoDisplay: IRepoDisplay
) : ViewModel() {
    private val listVideoSelect =
        savedStateHandle.get<List<VideoInfo>>(EditVideoFragment.VIDEO_INFO_EDIT_KEY)

    private var _listFeatureState =
        MutableStateFlow(FlowResult.newInstance<List<FeatureEditVideoDisplay>>())
    var listFeatureState = _listFeatureState.asStateFlow()

    private var _listCropState = MutableStateFlow(FlowResult.newInstance<List<CropDisplay>>())
    val listCropState = _listCropState.asStateFlow()
    var cropType = CROP_TYPE.TYPE_CUSTOM

    private var _listFilterState = MutableStateFlow(FlowResult.newInstance<List<FilterDisplay>>())
    val listFilterState = _listFilterState.asStateFlow()
    var filterType = FILTER_TYPE.ORIGINAL

    var maxDuration = LONG_DEFAULT
    val listPath: MutableList<String> = arrayListOf()

    var speedVideo: SPEED_TYPE = SPEED_TYPE.SPEED_1

    init {
        setUpSource()
        getListFeature()
    }

    private fun setUpSource() {
        listVideoSelect?.forEach {
            maxDuration += it.duration ?: LONG_DEFAULT
            listPath.add(it.thumbnailUrl.toString())
        }
    }

    private fun getListFeature() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repoDisplay.getListFeature()
            _listFeatureState.success(result)
        }
    }

    fun getListCrop() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repoDisplay.getListCrop(cropType)
            _listCropState.success(result)
        }
    }

    fun getListFilter() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repoDisplay.getListFilter(filterType)
            _listFilterState.success(result)
        }
    }
}
