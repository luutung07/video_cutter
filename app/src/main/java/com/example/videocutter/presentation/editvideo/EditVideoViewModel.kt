package com.example.videocutter.presentation.editvideo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.library_base.extension.LONG_DEFAULT
import com.example.videocutter.domain.model.VideoInfo

class EditVideoViewModel constructor(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val listVideoSelect =
        savedStateHandle.get<List<VideoInfo>>(EditVideoFragment.VIDEO_INFO_EDIT_KEY)

    var maxDuration = LONG_DEFAULT
    val listPath: MutableList<String> = arrayListOf()

    init {
        setUpSource()
    }

    private fun setUpSource() {
        listVideoSelect?.forEach {
            maxDuration += it.duration ?: LONG_DEFAULT
            listPath.add(it.thumbnailUrl.toString())
        }
    }

}
