package com.example.videocutter.presentation.repodisplay

import com.example.videocutter.presentation.repodisplay.model.CropDisplay
import com.example.videocutter.presentation.repodisplay.model.FILTER_TYPE
import com.example.videocutter.presentation.repodisplay.model.FeatureEditVideoDisplay
import com.example.videocutter.presentation.repodisplay.model.FilterDisplay
import com.example.videocutter.presentation.widget.crop.CROP_TYPE

interface IRepoDisplay {
    fun getListFeature(): List<FeatureEditVideoDisplay>
    fun getListCrop(type: CROP_TYPE): List<CropDisplay>
    fun getListFilter(filterType: FILTER_TYPE): List<FilterDisplay>
}
