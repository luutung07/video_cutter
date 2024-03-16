package com.example.videocutter.presentation.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.library_base.common.failure
import com.example.library_base.common.loading
import com.example.library_base.common.onException
import com.example.library_base.common.reset
import com.example.library_base.common.success
import com.example.library_base.common.usecase.FlowResult
import com.example.library_base.extension.LONG_DEFAULT
import com.example.videocutter.domain.model.Music
import com.example.videocutter.domain.usecase.GetListMusicUseCase
import com.example.videocutter.presentation.music.AddMusicAdapter.Companion.TYPE_NOPE
import com.example.videocutter.presentation.display.model.music.MUSIC_TYPE
import com.example.videocutter.presentation.display.model.music.MusicDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMusicViewModel @Inject constructor(
    private val getItunesListUseCase: GetListMusicUseCase
) : ViewModel() {

    private var listRaw: MutableList<Any> = arrayListOf()
    private var _musicListState = MutableStateFlow(FlowResult.newInstance<List<Any>>())
    val musicListState = _musicListState.asStateFlow()

    private var _durationState = MutableStateFlow(FlowResult.newInstance<Long>())
    val durationState = _durationState.asStateFlow()

    private var jobList: Job? = null

    private var isSelectNone = false

    private var mapCurrentPosition: HashMap<String?, Long?> = hashMapOf()

    private var idSelect: String? = null

    var idShowCropMusic: String? = null

    var idPlay: String? = null

    var musicType = MUSIC_TYPE.ITUNES

    var urlMp3: String? = null

    init {
        getListMusic()
    }

    private fun mapData() {
        val newList = listRaw.map {
            when (it) {
                is Pair<*, *> -> {
                    Pair(it.first, isSelectNone)
                }

                is Music -> {
                    MusicDisplay(
                        type = musicType,
                        data = it,
                        isSelect = idSelect == it.id,
                        isShowTrimMusic = idShowCropMusic == it.id,
                        isDownLoaded = musicType == MUSIC_TYPE.LOCAL,
                        isPlay = idPlay == it.id,
                        currentPosition = if (mapCurrentPosition.contains(it.id)) mapCurrentPosition[it.id] else null
                    )
                }

                else -> it
            }
        }.toList()
        _musicListState.success(newList)
    }

    private fun resetStateMusicDisplay() {
        viewModelScope.launch(Dispatchers.IO) {
            idShowCropMusic = null
            idPlay = null
            idSelect = null
            mapCurrentPosition.clear()
            mapData()
        }
    }

    fun getListMusic(isLoading: Boolean = false) {
        jobList?.cancel()
        jobList = viewModelScope.launch {
            val rv = GetListMusicUseCase.GetListMusicRV(musicType)
            getItunesListUseCase.invoke(rv)
                .onStart {
                    if (isLoading) {
                        _musicListState.loading()
                    }
                }
                .onException {
                    _musicListState.failure(it)
                }
                .collect {
                    listRaw.clear()
                    listRaw.add(Pair(TYPE_NOPE, false))
                    listRaw.addAll(it)
                    mapData()
                }
        }
    }

    fun updateStateNone(isSelect: Boolean) {
        isSelectNone = !isSelect
        resetStateMusicDisplay()
    }

    fun showCropMedia(id: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            idShowCropMusic = if (idShowCropMusic == id) {
                null
            } else {
                id
            }
            idPlay = id
            val item = listRaw.find {
                it is Music && it.id == id
            }
            if (item != null) {
                _durationState.success((item as Music).duration ?: LONG_DEFAULT)
            }
            mapData()
        }
    }

    fun resetDuration() {
        _durationState.reset()
    }

    fun updatePlay(id: String?, isSelect: Boolean = true) {
        idPlay = if (idPlay == id && isSelect) {
            null
        } else {
            id
        }
        mapData()
    }

    fun setCurrentPosition(id: String?, position: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            mapCurrentPosition[id] = position
            mapData()
        }
    }

    fun selectMusic(id: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            idSelect = if (idSelect == id) {
                null
            } else {
                id
            }
            idPlay = id
            idShowCropMusic = null
            isSelectNone = false
            mapData()
        }
    }
}
