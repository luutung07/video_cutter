package com.example.videocutter.presentation.display

import com.example.videocutter.presentation.display.model.editvideo.CropDisplay
import com.example.videocutter.presentation.display.model.editvideo.DetachFrameDisplay
import com.example.videocutter.presentation.display.model.editvideo.FILTER_TYPE
import com.example.videocutter.presentation.display.model.editvideo.FeatureEditVideoDisplay
import com.example.videocutter.presentation.display.model.editvideo.FilterDisplay
import com.example.videocutter.presentation.widget.crop.CROP_TYPE

interface IRepoDisplay {
    fun getListFeature(): List<FeatureEditVideoDisplay>
    fun getListCrop(type: CROP_TYPE): List<CropDisplay>
    fun getListFilter(filterType: FILTER_TYPE): List<FilterDisplay>
    fun getFrameDetach(list: List<String>, start: Long? = null, end: Long? = null): List<DetachFrameDisplay>
}
