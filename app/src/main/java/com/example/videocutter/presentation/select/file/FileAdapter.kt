package com.example.videocutter.presentation.select.file

import android.util.Log
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.example.baseapp.base.extension.getAppDrawable
import com.example.baseapp.base.extension.setOnSafeClick
import com.example.library_base.adapter.BaseGridAdapter
import com.example.library_base.adapter.BaseVH
import com.example.videocutter.R
import com.example.videocutter.common.extensions.loadImage
import com.example.videocutter.databinding.VideoSelectItemBinding
import com.example.videocutter.presentation.repodisplay.model.video.VideoInfoDisplay

class FileAdapter : BaseGridAdapter() {

    var listener: IFileListener? = null

    override fun getItemCountInRow(viewType: Int) = 4

    override fun getLayoutResource(viewType: Int) = R.layout.video_select_item

    override fun getDiffUtil(oldList: List<Any>, newList: List<Any>): DiffUtil.Callback {
        return FileDiffCallback(oldList, newList)
    }

    override fun onCreateViewHolder(viewType: Int, binding: ViewDataBinding): BaseVH<*>? {
        return FileVH(binding as VideoSelectItemBinding)
    }

    inner class FileVH(private val binding: VideoSelectItemBinding) :
        BaseVH<VideoInfoDisplay>(binding) {

        init {
            binding.clVideoSelectRoot.setOnSafeClick {
                val item = getDataAtPosition(absoluteAdapterPosition) as? VideoInfoDisplay
                if (item != null) {
                    if (item.isMaxSelect && !item.isSelect){
                        listener?.onMaxSelect()
                        return@setOnSafeClick
                    }
                    listener?.onSelect(item.getVideoInfo().id)
                }
            }

            binding.clVideoSelectRoot.setOnLongClickListener {
                val item = getDataAtPosition(absoluteAdapterPosition) as? VideoInfoDisplay
                if (item != null) {
                    listener?.onPReview(item.getVideoInfo().thumbnailUrl.toString())
                }
                true
            }
        }

        override fun onBind(data: VideoInfoDisplay) {
            super.onBind(data)
            binding.ivVideoSelectThumb.loadImage(data.getVideoInfo().thumbnailUrl.toString())
            selectFile(data)
            Log.d("TAG", "onBind: ${data.getVideoInfo().thumbnailUrl}")
            binding.tvVideoSelectTime.text = data.getVideoInfo().getTime()
        }

        override fun onBind(data: VideoInfoDisplay, payloads: List<Any>) {
            super.onBind(data, payloads)
            (payloads.firstOrNull() as? MutableList<*>)?.forEach {
                when (it) {
                    UPDATE_STATE_SELECT_FILE_PAYLOAD -> {
                        selectFile(data)
                    }
                }
            }
        }

        private fun selectFile(data: VideoInfoDisplay) {
            if (data.isSelect) {
                binding.ivVideoSelect.setImageDrawable(getAppDrawable(R.drawable.ic_select))
            } else {
                binding.ivVideoSelect.setImageDrawable(getAppDrawable(R.drawable.ic_un_select))
            }
        }
    }

    interface IFileListener {
        fun onSelect(id: Long?)
        fun onMaxSelect()
        fun onPReview(path: String)
    }
}
