package com.example.videocutter.presentation.editvideo.filter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.example.baseapp.base.extension.getAppColor
import com.example.baseapp.base.extension.getAppDrawable
import com.example.baseapp.base.extension.gone
import com.example.baseapp.base.extension.setOnSafeClick
import com.example.baseapp.base.extension.show
import com.example.library_base.adapter.BaseAdapter
import com.example.library_base.adapter.BaseDiffUtilCallback
import com.example.library_base.adapter.BaseVH
import com.example.videocutter.R
import com.example.videocutter.common.extensions.loadImage
import com.example.videocutter.databinding.FilterItemBinding
import com.example.videocutter.presentation.display.model.editvideo.FILTER_TYPE
import com.example.videocutter.presentation.display.model.editvideo.FilterDisplay

const val UPDATE_STATE_SELECT_PAYLOAD = "UPDATE_STATE_SELECT_PAYLOAD"

class FilterAdapter : BaseAdapter() {

    var listener: IFilterCallBack? = null

    override fun getLayoutResource(viewType: Int) = R.layout.filter_item

    override fun onCreateViewHolder(viewType: Int, binding: ViewDataBinding): BaseVH<*>? {
        return FilterVH(binding as FilterItemBinding)
    }

    override fun getDiffUtil(oldList: List<Any>, newList: List<Any>): DiffUtil.Callback {
        return FilterDiffCallback(oldList as List<FilterDisplay>, newList as List<FilterDisplay>)
    }

    inner class FilterVH(private val binding: FilterItemBinding) : BaseVH<FilterDisplay>(binding) {

        init {
            binding.root.setOnSafeClick {
                val item = getDataAtPosition(adapterPosition) as? FilterDisplay
                item?.filterType?.let {
                    listener?.onSelectFilter(it)
                }
            }
        }

        override fun onBind(data: FilterDisplay) {
            super.onBind(data)
            binding.tvFilter.text = data.getTitle()
            binding.ivFilter.loadImage(data.getImage())
            checkShowIcNope(data)
            stateSelect(data)
        }

        override fun onBind(data: FilterDisplay, payloads: List<Any>) {
            super.onBind(data, payloads)
            payloads.forEach {
                when (it) {
                    UPDATE_STATE_SELECT_PAYLOAD -> {
                        stateSelect(data)
                    }
                }
            }
        }

        private fun stateSelect(data: FilterDisplay) {
            if (data.isSelect == true) {
                binding.tvFilter.background =
                    getAppDrawable(R.drawable.shape_bg_color_1_corner_bottom_2)
                binding.tvFilter.setTextColor(getAppColor(R.color.white))
                checkShowIcNope(data)
            } else {
                binding.ivFilterNope.gone()
                binding.tvFilter.setTextColor(getAppColor(R.color.gray))
                binding.tvFilter.background = null
            }
        }

        private fun checkShowIcNope(data: FilterDisplay) {
            if (data.filterType == FILTER_TYPE.ORIGINAL) {
                binding.ivFilterNope.show()
            } else {
                binding.ivFilterNope.gone()
            }
        }
    }

    class FilterDiffCallback(oldList: List<FilterDisplay>, newList: List<FilterDisplay>) :
        BaseDiffUtilCallback<FilterDisplay>(oldList, newList) {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = getOldItem(oldItemPosition)
            val newItem = getNewItem(newItemPosition)
            return oldItem.filterType == newItem.filterType
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = getOldItem(oldItemPosition)
            val newItem = getNewItem(newItemPosition)
            return oldItem.isSelect == newItem.isSelect
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val oldItem = getOldItem(oldItemPosition)
            val newItem = getNewItem(newItemPosition)
            return if (oldItem.isSelect != newItem.isSelect) {
                UPDATE_STATE_SELECT_PAYLOAD
            } else {
                null
            }
        }
    }

    interface IFilterCallBack {
        fun onSelectFilter(type: FILTER_TYPE)
    }
}
