package com.example.videocutter.presentation.repodisplay.repo

import android.media.MediaMetadataRetriever
import android.net.Uri
import com.example.library_base.extension.getApplication
import com.example.videocutter.presentation.repodisplay.IRepoDisplay
import com.example.videocutter.presentation.repodisplay.model.editvideo.CropDisplay
import com.example.videocutter.presentation.repodisplay.model.editvideo.DetachFrameDisplay
import com.example.videocutter.presentation.repodisplay.model.editvideo.FEATURE_TYPE
import com.example.videocutter.presentation.repodisplay.model.editvideo.FILTER_TYPE
import com.example.videocutter.presentation.repodisplay.model.editvideo.FeatureEditVideoDisplay
import com.example.videocutter.presentation.repodisplay.model.editvideo.FilterDisplay
import com.example.videocutter.presentation.widget.crop.CROP_TYPE
import javax.inject.Inject

class RepoDisplayImpl @Inject constructor() : IRepoDisplay {
    override fun getListFeature(): List<FeatureEditVideoDisplay> {
        val list: MutableList<FeatureEditVideoDisplay> = arrayListOf()
        list.add(FeatureEditVideoDisplay(FEATURE_TYPE.CROP))
        list.add(FeatureEditVideoDisplay(FEATURE_TYPE.CUT))
        list.add(FeatureEditVideoDisplay(FEATURE_TYPE.SPEED))
        list.add(FeatureEditVideoDisplay(FEATURE_TYPE.FILTER))
        list.add(FeatureEditVideoDisplay(FEATURE_TYPE.MUSIC))
        list.add(FeatureEditVideoDisplay(FEATURE_TYPE.ROTATE))
        return list
    }

    override fun getListCrop(type: CROP_TYPE): List<CropDisplay> {
        val list: MutableList<CropDisplay> = arrayListOf()
        list.add(CropDisplay(CROP_TYPE.TYPE_CUSTOM, isSelect = type == CROP_TYPE.TYPE_CUSTOM))
        list.add(CropDisplay(CROP_TYPE.TYPE_INSTAGRAM, isSelect = type == CROP_TYPE.TYPE_INSTAGRAM))
        list.add(CropDisplay(CROP_TYPE.TYPE_4_5, isSelect = type == CROP_TYPE.TYPE_4_5))
        list.add(CropDisplay(CROP_TYPE.TYPE_5_4, isSelect = type == CROP_TYPE.TYPE_5_4))
        list.add(CropDisplay(CROP_TYPE.TYPE_9_16, isSelect = type == CROP_TYPE.TYPE_9_16))
        list.add(CropDisplay(CROP_TYPE.TYPE_16_9, isSelect = type == CROP_TYPE.TYPE_16_9))
        list.add(CropDisplay(CROP_TYPE.TYPE_3_2, isSelect = type == CROP_TYPE.TYPE_3_2))
        list.add(CropDisplay(CROP_TYPE.TYPE_2_3, isSelect = type == CROP_TYPE.TYPE_2_3))
        list.add(CropDisplay(CROP_TYPE.TYPE_4_3, isSelect = type == CROP_TYPE.TYPE_4_3))
        list.add(CropDisplay(CROP_TYPE.TYPE_3_4, isSelect = type == CROP_TYPE.TYPE_3_4))
        return list
    }

    override fun getListFilter(filterType: FILTER_TYPE): List<FilterDisplay> {
        val list: MutableList<FilterDisplay> = arrayListOf()
        list.add(
            FilterDisplay(
                filterType = FILTER_TYPE.ORIGINAL,
                isSelect = filterType == FILTER_TYPE.ORIGINAL
            )
        )
        list.add(
            FilterDisplay(
                filterType = FILTER_TYPE.SPRING,
                isSelect = filterType == FILTER_TYPE.SPRING
            )
        )
        list.add(
            FilterDisplay(
                filterType = FILTER_TYPE.SUMMER,
                isSelect = filterType == FILTER_TYPE.SUMMER
            )
        )
        list.add(
            FilterDisplay(
                filterType = FILTER_TYPE.FALL,
                isSelect = filterType == FILTER_TYPE.FALL
            )
        )
        list.add(
            FilterDisplay(
                filterType = FILTER_TYPE.WINTER,
                isSelect = filterType == FILTER_TYPE.WINTER
            )
        )
        return list
    }

    override fun getFrameDetach(list: List<String>): List<DetachFrameDisplay> {
        val result: MutableList<DetachFrameDisplay> = arrayListOf()
        return try {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            list.forEach {
                mediaMetadataRetriever.setDataSource(
                    getApplication(),
                    Uri.parse(it)
                )
                // Retrieve media data use microsecond
                val durationStr =
                    mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val duration = durationStr!!.toLong()
                for (i in 0 until duration step 1000) {
                    val bitmap =
                        mediaMetadataRetriever.getFrameAtTime(
                            i * 1000,
                            MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                        )
                    try {
                        bitmap?.let { result.add(DetachFrameDisplay(it, i)) }
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                }
            }
            mediaMetadataRetriever.release()
            result
        } catch (e: Throwable) {
            throw e
        }
    }
}
