package com.example.videocutter.presentation.home

import androidx.databinding.ViewDataBinding
import com.example.library_base.adapter.BaseAdapter
import com.example.library_base.adapter.BaseVH
import com.example.videocutter.R
import com.example.videocutter.databinding.VideoRecentItemBinding
import com.example.videocutter.domain.model.VideoInfo

class HomeAdapter : BaseAdapter() {
    override fun getLayoutResource(viewType: Int) = R.layout.video_recent_item

    override fun onCreateViewHolder(viewType: Int, binding: ViewDataBinding): BaseVH<*>? {
        return HomeVH(binding as VideoRecentItemBinding)
    }

    inner class HomeVH(private val binding: VideoRecentItemBinding) : BaseVH<VideoInfo>(binding) {
        override fun onBind(data: VideoInfo) {
            super.onBind(data)
        }
    }
}
