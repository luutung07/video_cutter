package com.example.videocutter.presentation.adjust

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.library_base.common.success
import com.example.library_base.common.usecase.FlowResult
import com.example.library_base.extension.LONG_DEFAULT
import com.example.videocutter.domain.model.VideoInfo
import com.example.videocutter.presentation.VideoInfoDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class AdjustViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {

    var listVideoInfoSelected: List<VideoInfo>? =
        savedStateHandle.get<List<VideoInfo>>(AdjustFragment.PATHS_KEY)

    val listPath: MutableList<String> = arrayListOf()
    var maxDuration: Long = LONG_DEFAULT
    var isStart: Boolean = true

    private var _videoListState = MutableStateFlow(FlowResult.newInstance<List<Any>>())
    val videoListState = _videoListState.asStateFlow()

    init {
        setUpSourceVideoInfo()
        getVideoList()
    }

    private fun getVideoList() {
        viewModelScope.launch(Dispatchers.IO) {
            val result: MutableList<Any> = arrayListOf()
            listVideoInfoSelected?.forEach {
                result.add(
                    VideoInfoDisplay(
                        data = it,
                        isSelect = false
                    )
                )
            }
            result.add(AdjustAdapter.ITEM_ADD)
            _videoListState.success(result)
        }
    }


    private fun setUpSourceVideoInfo() {
        maxDuration = 0
        listPath.clear()
        listVideoInfoSelected?.forEach {
            listPath.add(it.thumbnailUrl.toString())
            it.duration?.let { duration ->
                maxDuration += duration
            }
        }
    }

    fun swapVideo(oldIndex: Int, newIndex: Int) {
        viewModelScope.launch {
            if (listVideoInfoSelected != null) {
                Collections.swap(listVideoInfoSelected!!, oldIndex, newIndex)
                getVideoList()
            }
        }
    }

    fun deleteVideo(id: Long?) {
        viewModelScope.launch(Dispatchers.IO) {
            val index = listVideoInfoSelected?.indexOfFirst {
                it.id == id
            }
            if (index != null && index >= 0) {
                val newList = listVideoInfoSelected?.toMutableList()
                listVideoInfoSelected = newList
                newList?.removeAt(index)
                getVideoList()
                setUpSourceVideoInfo()
            }
        }
    }
}
