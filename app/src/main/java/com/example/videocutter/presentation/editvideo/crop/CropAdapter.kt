package com.example.videocutter.presentation.editvideo.crop

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.example.baseapp.base.extension.setOnSafeClick
import com.example.library_base.adapter.BaseAdapter
import com.example.library_base.adapter.BaseDiffUtilCallback
import com.example.library_base.adapter.BaseVH
import com.example.videocutter.R
import com.example.videocutter.common.extensions.loadImage
import com.example.videocutter.databinding.CropVideoItemBinding
import com.example.videocutter.presentation.repodisplay.model.editvideo.CropDisplay
import com.example.videocutter.presentation.widget.crop.CROP_TYPE

class CropAdapter : BaseAdapter() {

    var listener: ICropCallBack? = null

    override fun getLayoutResource(viewType: Int): Int {
        return R.layout.crop_video_item
    }

    override fun onCreateViewHolder(viewType: Int, binding: ViewDataBinding): BaseVH<*>? {
        return CropVH(binding as CropVideoItemBinding)
    }

    override fun getDiffUtil(oldList: List<Any>, newList: List<Any>): DiffUtil.Callback {
        return CropDiffCallBack(oldList, newList)
    }

    inner class CropVH(private val binding: CropVideoItemBinding) :
        BaseVH<CropDisplay>(binding) {

        init {
            binding.root.setOnSafeClick {
                val item = getDataAtPosition(adapterPosition) as? CropDisplay
                item?.cropType?.let {
                    listener?.onCropType(it)
                }
            }
        }

        override fun onBind(data: CropDisplay) {
            super.onBind(data)
            binding.ivCropVideo.loadImage(data.getIcon())
            binding.tvCropVideo.text = data.getName()
        }
    }

    class CropDiffCallBack(oldList: List<Any>, newList: List<Any>) :
        BaseDiffUtilCallback<Any>(oldList, newList) {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = getOldItem(oldItemPosition) as? CropDisplay
            val newItem = getNewItem(newItemPosition) as? CropDisplay
            return oldItem?.cropType == newItem?.cropType
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = getOldItem(oldItemPosition) as? CropDisplay
            val newItem = getNewItem(newItemPosition) as? CropDisplay
            return oldItem?.isSelect == newItem?.isSelect
        }
    }

    interface ICropCallBack {
        fun onCropType(type: CROP_TYPE)
    }
}
