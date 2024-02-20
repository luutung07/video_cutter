package com.example.videocutter.presentation.editvideo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.library_base.common.success
import com.example.library_base.common.usecase.FlowResult
import com.example.library_base.extension.LONG_DEFAULT
import com.example.videocutter.domain.model.VideoInfo
import com.example.videocutter.presentation.repodisplay.IRepoDisplay
import com.example.videocutter.presentation.repodisplay.model.FeatureEditVideoDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
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

    var maxDuration = LONG_DEFAULT
    val listPath: MutableList<String> = arrayListOf()

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
}
