package com.example.videocutter.presentation.music

import android.util.Log
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
import com.example.videocutter.presentation.display.model.music.MusicState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@HiltViewModel
class AddMusicViewModel @Inject constructor(
    private val getItunesListUseCase: GetListMusicUseCase
) : ViewModel() {

    private var listRaw: MutableList<Any> = arrayListOf()
    private var _musicListState = MutableStateFlow(FlowResult.newInstance<List<Any>>())
    val musicListState = _musicListState.asStateFlow()

    private var _timeLineState = MutableStateFlow(FlowResult.newInstance<Pair<Long, Long>>())
    val timeLineState = _timeLineState.asStateFlow()

    private var jobList: Job? = null
    private var jobProgress: Job? = null

    private var mapState: HashMap<String?, MusicState> = hashMapOf()

    private var isSelectNone = false

    var idShowCropMusic: String? = null

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
                        musicState = mapState.getOrDefault(it.id, MusicState()).copy(
                            isDownLoaded = musicType == MUSIC_TYPE.LOCAL,
                            end = it.duration
                        )
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
            mapState.clear()
            mapData()
        }
    }

    private fun clearState(hasClearSelect: Boolean = false) {
        mapState.forEach { (_, v) ->
            if (hasClearSelect) v.isSelect = false
            v.isPlay = false
            v.isShowTrimMusic = false
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
            idShowCropMusic = id

            val music = listRaw.find {
                it is Music && it.id == id
            }
            if (music != null) {
                clearState(false)
                if (mapState.containsKey(id)) {
                    val newState = mapState[id]!!.copy(isShowTrimMusic = true, isPlay = true)
                    mapState[id] = newState
                } else {
                    mapState[id] = MusicState(
                        isShowTrimMusic = true,
                        isPlay = true,
                        isDownLoaded = musicType == MUSIC_TYPE.LOCAL,
                        start = LONG_DEFAULT,
                        end = (music as Music).duration
                    )
                }

                _timeLineState.success(
                    Pair(
                        mapState[id]!!.start ?: LONG_DEFAULT,
                        mapState[id]!!.end ?: LONG_DEFAULT
                    )
                )

                mapData()
            }
        }
    }

    fun resetTimeline() {
        _timeLineState.reset()
    }

    fun updatePlay(id: String?, isSelect: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {

            val newStatePlay = if (isSelect) {
                !mapState[id]!!.isPlay
            } else {
                true
            }
            mapState[id] = mapState[id]!!.copy(isPlay = newStatePlay)

            mapData()
        }
    }

    fun setCurrentPosition(id: String?, position: Long, start: Long, end: Long) {
        jobProgress?.cancel()
        jobProgress = viewModelScope.launch(Dispatchers.IO) {
            val newState = mapState[id]!!
            newState.start = start
            newState.currentPosition = position
            newState.end = end
            mapState[id] = newState
            mapData()
        }
    }

    fun doneCrop(id: String?) {
        val newState = mapState[id]!!
        newState.isShowTrimMusic = false
        mapState[id] = newState
        mapData()
    }

    fun selectMusic(id: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            clearState(true)
            idShowCropMusic = id

            val music = listRaw.find {
                it is Music && it.id == id
            }

            if (music != null) {
                if (mapState.containsKey(id)) {
                    val newState = mapState[id]!!.copy(isSelect = true)
                    mapState[id] = newState
                } else {
                    mapState[id] = MusicState(
                        isSelect = true,
                        isPlay = true,
                        isDownLoaded = musicType == MUSIC_TYPE.LOCAL,
                        end = (music as Music).duration
                    )
                }

                _timeLineState.success(
                    Pair(
                        mapState[id]!!.start ?: LONG_DEFAULT,
                        mapState[id]!!.end ?: LONG_DEFAULT
                    )
                )

                isSelectNone = false
                mapData()
            }
        }
    }

    fun isPlaying(id: String?): Boolean = mapState.getOrDefault(id, MusicState()).isPlay
}
