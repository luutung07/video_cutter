package com.example.videocutter.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.library_base.common.failure
import com.example.library_base.common.reset
import com.example.library_base.common.success
import com.example.library_base.common.usecase.FlowResult
import com.example.videocutter.presentation.display.IRepoDisplay
import com.example.videocutter.presentation.display.model.editvideo.DetachFrameDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repoDisplay: IRepoDisplay
) : ViewModel() {

    private var _listFrameDetach = MutableStateFlow(FlowResult.newInstance<List<DetachFrameDisplay>>())
    val listFrameDetach = _listFrameDetach.asStateFlow()

    init {

    }

    fun detachFrameVideo(list: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repoDisplay.getFrameDetach(list)
                _listFrameDetach.success(result)
            }catch (e: Exception){
                _listFrameDetach.failure(e)
            }
        }
    }

    fun resetDetach() {
        _listFrameDetach.reset()
    }
}
