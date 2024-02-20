package com.example.videocutter.presentation.repodisplay.repo

import com.example.baseapp.base.extension.getAppDrawable
import com.example.baseapp.base.extension.getAppString
import com.example.videocutter.R
import com.example.videocutter.presentation.repodisplay.IRepoDisplay
import com.example.videocutter.presentation.repodisplay.model.FeatureEditVideoDisplay
import javax.inject.Inject

class RepoDisplayImpl @Inject constructor() : IRepoDisplay {
    override fun getListFeature(): List<FeatureEditVideoDisplay> {
        val list: MutableList<FeatureEditVideoDisplay> = arrayListOf()
        list.add(
            FeatureEditVideoDisplay(
                resource = getAppDrawable(R.drawable.ic_crop),
                name = getAppString(R.string.feature_crop)
            )
        )

        list.add(
            FeatureEditVideoDisplay(
                resource = getAppDrawable(R.drawable.ic_cut),
                name = getAppString(R.string.feature_cut)
            )
        )

        list.add(
            FeatureEditVideoDisplay(
                resource = getAppDrawable(R.drawable.ic_speed),
                name = getAppString(R.string.feature_speed)
            )
        )

        list.add(
            FeatureEditVideoDisplay(
                resource = getAppDrawable(R.drawable.ic_filter),
                name = getAppString(R.string.feature_filter)
            )
        )

        list.add(
            FeatureEditVideoDisplay(
                resource = getAppDrawable(R.drawable.ic_music),
                name = getAppString(R.string.feature_music)
            )
        )

        list.add(
            FeatureEditVideoDisplay(
                resource = getAppDrawable(R.drawable.ic_rotate),
                name = getAppString(R.string.feature_rotate)
            )
        )

        return list
    }
}
