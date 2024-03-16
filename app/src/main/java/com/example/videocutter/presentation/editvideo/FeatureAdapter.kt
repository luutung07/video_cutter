package com.example.videocutter.presentation.editvideo

import androidx.databinding.ViewDataBinding
import com.example.baseapp.base.extension.setOnSafeClick
import com.example.library_base.adapter.BaseAdapter
import com.example.library_base.adapter.BaseVH
import com.example.videocutter.R
import com.example.videocutter.common.extensions.loadImage
import com.example.videocutter.databinding.FeatureEditVideoItemBinding
import com.example.videocutter.presentation.display.model.editvideo.FEATURE_TYPE
import com.example.videocutter.presentation.display.model.editvideo.FeatureEditVideoDisplay

class FeatureAdapter : BaseAdapter() {

    var listener: IFeatureCallback? = null

    override fun getLayoutResource(viewType: Int) = R.layout.feature_edit_video_item

    override fun onCreateViewHolder(viewType: Int, binding: ViewDataBinding): BaseVH<*>? {
        return FeatureEditVH(binding as FeatureEditVideoItemBinding)
    }

    inner class FeatureEditVH(private val binding: FeatureEditVideoItemBinding) :
        BaseVH<FeatureEditVideoDisplay>(binding) {

        init {
            binding.clFeatureEditVideoRoot.setOnSafeClick {
                val item = getDataAtPosition(adapterPosition) as? FeatureEditVideoDisplay
                item?.featureType?.let {
                    listener?.onFeature(it)
                }
            }
        }

        override fun onBind(data: FeatureEditVideoDisplay) {
            super.onBind(data)
            binding.ivFeatureEditVideo.loadImage(data.getIcon())
            binding.tvFeatureEditVideoName.text = data.getName()
        }
    }

    interface IFeatureCallback {
        fun onFeature(type: FEATURE_TYPE)
    }
}
