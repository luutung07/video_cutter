package com.example.videocutter.presentation.editvideo

import androidx.databinding.ViewDataBinding
import com.example.library_base.adapter.BaseAdapter
import com.example.library_base.adapter.BaseVH
import com.example.videocutter.R
import com.example.videocutter.common.extensions.loadImage
import com.example.videocutter.databinding.FeatureEditVideoItemBinding
import com.example.videocutter.presentation.repodisplay.model.FeatureEditVideoDisplay

class FeatureAdapter : BaseAdapter() {
    override fun getLayoutResource(viewType: Int) = R.layout.feature_edit_video_item

    override fun onCreateViewHolder(viewType: Int, binding: ViewDataBinding): BaseVH<*>? {
        return FeatureEditVH(binding as FeatureEditVideoItemBinding)
    }

    inner class FeatureEditVH(private val binding: FeatureEditVideoItemBinding) :
        BaseVH<FeatureEditVideoDisplay>(binding) {
        override fun onBind(data: FeatureEditVideoDisplay) {
            super.onBind(data)
            binding.ivFeatureEditVideo.loadImage(data.resource)
            binding.tvFeatureEditVideoName.text = data.name
        }
    }
}
