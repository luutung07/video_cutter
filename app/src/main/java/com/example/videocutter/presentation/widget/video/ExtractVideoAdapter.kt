package com.example.videocutter.presentation.widget.video

import android.graphics.Bitmap
import androidx.databinding.ViewDataBinding
import com.example.library_base.adapter.BaseAdapter
import com.example.library_base.adapter.BaseVH
import com.example.videocutter.R
import com.example.videocutter.databinding.ExtratVideoItemBinding

class ExtractVideoAdapter : BaseAdapter() {
    override fun getLayoutResource(viewType: Int) = R.layout.extrat_video_item

    override fun onCreateViewHolder(viewType: Int, binding: ViewDataBinding): BaseVH<*>? {
        return ExtractVideoVh(binding as ExtratVideoItemBinding)
    }

    inner class ExtractVideoVh(private val binding: ExtratVideoItemBinding) :
        BaseVH<Bitmap>(binding) {
        override fun onBind(data: Bitmap) {
            super.onBind(data)
            binding.ivExtractVideo.setImageBitmap(data)
        }
    }
}
