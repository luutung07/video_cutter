package com.example.videocutter.presentation

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.library_base.common.failure
import com.example.library_base.common.onException
import com.example.library_base.common.reset
import com.example.library_base.common.success
import com.example.library_base.common.usecase.FlowResult
import com.example.videocutter.domain.model.VideoInfo
import com.example.videocutter.domain.repo.IVideoRepo
import com.example.videocutter.domain.usecase.GetListFrameDetachUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getListFrameDetachUseCase: GetListFrameDetachUseCase
) : ViewModel() {

    private var _listFrameDetach = MutableStateFlow(FlowResult.newInstance<List<Bitmap>>())
    val listFrameDetach = _listFrameDetach.asStateFlow()

    init {

    }

    fun detachFrameVideo(list: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val rv = GetListFrameDetachUseCase.GetListFrameDetachRV(list)
            getListFrameDetachUseCase.invoke(rv)
                .onException {
                    _listFrameDetach.failure(it)
                }
                .collect {
                    _listFrameDetach.success(it)
                }
        }
    }

    fun resetDetach() {
        _listFrameDetach.reset()
    }
}
