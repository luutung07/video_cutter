package com.example.videocutter.presentation.select

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.example.baseapp.base.extension.setOnSafeClick
import com.example.library_base.adapter.BaseAdapter
import com.example.library_base.adapter.BaseVH
import com.example.videocutter.R
import com.example.videocutter.common.extensions.loadImage
import com.example.videocutter.databinding.VideoSelectedItemBinding
import com.example.videocutter.domain.model.VideoInfo

class SelectedAdapter : BaseAdapter() {

    var listener: ISelectCallBack? = null

    override fun getLayoutResource(viewType: Int) = R.layout.video_selected_item

    override fun getDiffUtil(oldList: List<Any>, newList: List<Any>): DiffUtil.Callback {
        return SelectDiffCallback(oldList, newList)
    }

    override fun onCreateViewHolder(viewType: Int, binding: ViewDataBinding): BaseVH<*>? {
        return SelectedVH(binding as VideoSelectedItemBinding)
    }

    inner class SelectedVH(private val binding: VideoSelectedItemBinding) :
        BaseVH<VideoInfo>(binding) {

        init {
            binding.ivVideoSelectedDelete.setOnSafeClick {
                val item = getDataAtPosition(adapterPosition) as? VideoInfo
                if (item != null) {
                    listener?.onDelete(item.id)
                }
            }
        }

        override fun onBind(data: VideoInfo) {
            super.onBind(data)
            binding.ivVideoSelected.loadImage(data.thumbnailUrl.toString())
            binding.tvVideoSelectedTime.text = data.getTime()
        }
    }

    interface ISelectCallBack {
        fun onDelete(id: Long?)
    }
}
