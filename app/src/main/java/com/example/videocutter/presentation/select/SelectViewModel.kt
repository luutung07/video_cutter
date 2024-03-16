package com.example.videocutter.presentation.select

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.library_base.common.BaseUseCase
import com.example.library_base.common.failure
import com.example.library_base.common.loading
import com.example.library_base.common.onException
import com.example.library_base.common.success
import com.example.library_base.common.usecase.FlowResult
import com.example.videocutter.AppConfig
import com.example.videocutter.AppConfig.TIME_DELAY
import com.example.videocutter.common.datapage.VideoDataPage
import com.example.videocutter.domain.model.VideoInfo
import com.example.videocutter.domain.usecase.GetFileVideoUseCase
import com.example.videocutter.domain.usecase.GetFolderVideoUseCase
import com.example.videocutter.presentation.display.model.video.VideoInfoDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class SelectViewModel @Inject constructor(
    private val folderUseCase: GetFolderVideoUseCase,
    private val fileUseCase: GetFileVideoUseCase
) : ViewModel() {
    var isOpenFile = true
    var maxItem = false

    private var _folderState = MutableStateFlow(FlowResult.newInstance<List<VideoInfoDisplay>>())
    val folderState = _folderState.asStateFlow()
    private val listFolderRoot: MutableList<VideoInfo> = arrayListOf()
    var idSelectFolder: Long? = null

    private var _fileState = MutableStateFlow(FlowResult.newInstance<List<VideoInfoDisplay>>())
    val fileState = _fileState.asStateFlow()
    val dataPage = VideoDataPage<VideoInfo>()
    private var setFileSelect: HashSet<Long?> = HashSet()

    var listSelect: MutableList<VideoInfo> = arrayListOf()
    private var _selectFileState =
        MutableStateFlow(FlowResult.newInstance<List<VideoInfo>>())
    val selectFileState = _selectFileState.asStateFlow()

    init {
        getFolder()
        getFiles()
    }

    private fun mapFolder() {
        val result = listFolderRoot.map {
            if (it.id == idSelectFolder) {
                VideoInfoDisplay(
                    data = it,
                    isSelect = true
                )
            } else {
                VideoInfoDisplay(
                    data = it,
                    isSelect = false
                )
            }
        }.toList()
        _folderState.success(result)
    }

    private fun mapFile() {
        val result = dataPage.dataList.map {
            if (setFileSelect.contains(it.id)) {
                VideoInfoDisplay(
                    data = it,
                    isSelect = true,
                    isMaxSelect = maxItem
                )
            } else {
                VideoInfoDisplay(
                    data = it,
                    isSelect = false,
                    isMaxSelect = maxItem
                )
            }
        }.toList()
        _fileState.success(result)
    }

    private fun getFolder() {
        viewModelScope.launch {
            folderUseCase.invoke(BaseUseCase.VoidRequest())
                .onStart {
                    _folderState.loading()
                }
                .onException {
                    _folderState.failure(it)
                }
                .collect {
                    listFolderRoot.addAll(it)
                    mapFolder()
                }
        }
    }

    fun getFiles() {
        viewModelScope.launch {
            val rv = GetFileVideoUseCase.GetFileVideoRv(id = idSelectFolder).apply {
                this.page = dataPage.nextPageIndex + 1
            }
            fileUseCase.invoke(rv)
                .onStart {
                    _fileState.loading()
                }
                .onException {
                    _fileState.failure(it)
                }
                .collect {
                    delay(TIME_DELAY)
                    dataPage.addList(it)
                    mapFile()
                }
        }
    }

    fun selectFolder(id: Long?) {
        viewModelScope.launch(Dispatchers.IO) {
            idSelectFolder = id
            mapFolder()
            dataPage.reset()
            getFiles()
        }
    }

    fun selectFile(id: Long?) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = dataPage.dataList.toMutableList()

            when (setFileSelect.contains(id)) {
                true -> {
                    setFileSelect.remove(id)
                    val indexItem = listSelect.indexOfFirst {
                        it.id == id
                    }
                    if (indexItem >= 0) {
                        listSelect.removeAt(indexItem)
                    }
                }

                else -> {
                    setFileSelect.add(id)
                    val item = list.find {
                        it.id == id
                    }
                    if (item != null) {
                        listSelect.add(item)
                    }
                }
            }
            maxItem = listSelect.count() == AppConfig.MAX_ITEM_SELECT
            _selectFileState.success(listSelect)
            mapFile()
        }
    }

    fun setSelectFile(newList: List<VideoInfo>) {
        listSelect.clear()
        setFileSelect.clear()

        newList.forEach { data ->
            setFileSelect.add(data.id)
        }
        listSelect.addAll(newList)

        maxItem = listSelect.count() == AppConfig.MAX_ITEM_SELECT
        _selectFileState.success(listSelect)
        mapFile()
    }

    fun swap(oldIndex: Int, newIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Collections.swap(listSelect, oldIndex, newIndex)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("checkHilt", "onCleared: SelectViewModel")
    }
}
