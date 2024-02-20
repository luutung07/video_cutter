package com.example.videocutter.presentation.adjust

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.example.baseapp.base.extension.setOnSafeClick
import com.example.library_base.adapter.BaseAdapter
import com.example.library_base.adapter.BaseVH
import com.example.videocutter.R
import com.example.videocutter.common.extensions.loadImage
import com.example.videocutter.databinding.ItemAddVideoBinding
import com.example.videocutter.databinding.VideoSelectedItemBinding
import com.example.videocutter.domain.model.VideoInfo
import com.example.videocutter.presentation.repodisplay.model.VideoInfoDisplay

class AdjustAdapter : BaseAdapter() {

    companion object {

        private const val ITEM_ADD_MORE_TYPE = 0
        private const val ITEM_DATA_TYPE = 1

        const val ITEM_ADD = "ITEM_ADD"
    }

    var listener: IAdjustCallBack? = null

    override fun getLayoutResource(viewType: Int): Int {
        return when (viewType) {
            ITEM_DATA_TYPE -> R.layout.video_selected_item
            ITEM_ADD_MORE_TYPE -> R.layout.item_add_video
            else -> INVALID_RESOURCE
        }
    }

    override fun getItemViewTypeCustom(position: Int): Int {
        return when (getDataAtPosition(position)) {
            is VideoInfoDisplay -> ITEM_DATA_TYPE
            is String -> ITEM_ADD_MORE_TYPE
            else -> NORMAL_VIEW_TYPE
        }
    }

    override fun getDiffUtil(oldList: List<Any>, newList: List<Any>): DiffUtil.Callback {
        return AdjustDiffCallback(oldList, newList)
    }

    override fun onCreateViewHolder(viewType: Int, binding: ViewDataBinding): BaseVH<*>? {
        return when (viewType) {
            ITEM_DATA_TYPE -> SelectedVH(binding as VideoSelectedItemBinding)
            ITEM_ADD_MORE_TYPE -> AddVideoVH(binding as ItemAddVideoBinding)
            else -> null
        }
    }

    inner class SelectedVH(private val binding: VideoSelectedItemBinding) :
        BaseVH<VideoInfoDisplay>(binding) {

        init {
            binding.ivVideoSelectedDelete.setOnSafeClick {
                val item = getDataAtPosition(absoluteAdapterPosition) as? VideoInfoDisplay
                if (item != null) {
                    listener?.onDelete(item.getVideoInfo().id, item.getVideoInfo().thumbnailUrl.toString())
                }
            }
        }

        override fun onBind(data: VideoInfoDisplay) {
            super.onBind(data)
            binding.ivVideoSelected.loadImage(data.getVideoInfo().thumbnailUrl.toString())
            binding.tvVideoSelectedTime.text = data.getVideoInfo().getTime()
        }
    }

    inner class AddVideoVH(private val binding: ItemAddVideoBinding) :
        BaseVH<VideoInfo>(binding) {

        init {
            binding.ivVideoSelected.setOnSafeClick {
                listener?.onAddMore()
            }
        }
    }

    interface IAdjustCallBack {
        fun onDelete(id: Long?, path: String)
        fun onAddMore()
    }
}
