package com.example.videocutter.presentation.select.folder

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.example.baseapp.base.extension.getAppColor
import com.example.baseapp.base.extension.getAppString
import com.example.baseapp.base.extension.setOnSafeClick
import com.example.library_base.adapter.BaseAdapter
import com.example.library_base.adapter.BaseVH
import com.example.library_base.extension.INT_DEFAULT
import com.example.videocutter.R
import com.example.videocutter.common.extensions.loadImage
import com.example.videocutter.databinding.FolderVideoItemBinding
import com.example.videocutter.presentation.VideoInfoDisplay

class FolderAdapter : BaseAdapter() {

    var listener: IFolderCallBack? = null

    override fun getLayoutResource(viewType: Int) = R.layout.folder_video_item

    override fun onCreateViewHolder(viewType: Int, binding: ViewDataBinding): BaseVH<*>? {
        return FolderVH(binding as FolderVideoItemBinding)
    }

    override fun getDiffUtil(oldList: List<Any>, newList: List<Any>): DiffUtil.Callback {
        return FolderDiffCallback(oldList, newList)
    }

    inner class FolderVH(private val binding: FolderVideoItemBinding) :
        BaseVH<VideoInfoDisplay>(binding) {

        init {
            binding.root.setOnSafeClick {
                val item = getDataAtPosition(absoluteAdapterPosition) as? VideoInfoDisplay
                if (item != null) {
                    listener?.onSelect(item.getVideoInfo().id, item.getVideoInfo().name.toString())
                }
            }
        }

        override fun onBind(data: VideoInfoDisplay) {
            super.onBind(data)
            binding.tvFolderVideoName.text = data.getVideoInfo().name
            binding.ivFolderVideoThumb.loadImage(data.getVideoInfo().thumbnailUrl.toString())
            binding.tvFolderVideoCount.text =
                getAppString(R.string.count_file, data.getVideoInfo().count ?: INT_DEFAULT)
            changeStateBackground(data)
        }

        override fun onBind(data: VideoInfoDisplay, payloads: List<Any>) {
            super.onBind(data, payloads)
            (payloads.firstOrNull() as? List<*>)?.forEach {
                when (it) {
                    UPDATE_STATE_SELECT_FOLDER_PAYLOAD -> changeStateBackground(data)
                }
            }
        }

        private fun changeStateBackground(data: VideoInfoDisplay) {
            if (data.isSelect) {
                binding.clFolderVideoRoot.setBackgroundColor(getAppColor(R.color.ink_10))
            } else {
                binding.clFolderVideoRoot.setBackgroundColor(getAppColor(R.color.transparent))
            }
        }
    }

    interface IFolderCallBack {
        fun onSelect(id: Long?,name: String)
    }
}
