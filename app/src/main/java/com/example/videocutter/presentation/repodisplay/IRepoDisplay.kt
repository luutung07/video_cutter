package com.example.videocutter.presentation.repodisplay

import com.example.videocutter.presentation.repodisplay.model.editvideo.CropDisplay
import com.example.videocutter.presentation.repodisplay.model.editvideo.DetachFrameDisplay
import com.example.videocutter.presentation.repodisplay.model.editvideo.FILTER_TYPE
import com.example.videocutter.presentation.repodisplay.model.editvideo.FeatureEditVideoDisplay
import com.example.videocutter.presentation.repodisplay.model.editvideo.FilterDisplay
import com.example.videocutter.presentation.widget.crop.CROP_TYPE

interface IRepoDisplay {
    fun getListFeature(): List<FeatureEditVideoDisplay>
    fun getListCrop(type: CROP_TYPE): List<CropDisplay>
    fun getListFilter(filterType: FILTER_TYPE): List<FilterDisplay>
    fun getFrameDetach(list: List<String>): List<DetachFrameDisplay>
}
