package com.example.videocutter.presentation.select

import com.example.library_base.adapter.BaseDiffUtilCallback
import com.example.videocutter.domain.model.VideoInfo

class SelectDiffCallback(oldList: List<Any>, newList: List<Any>) :
    BaseDiffUtilCallback<Any>(oldList, newList) {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = getOldItem(oldItemPosition) as? VideoInfo
        val newItem = getNewItem(newItemPosition) as? VideoInfo
        return oldItem?.id == newItem?.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = getOldItem(oldItemPosition) as? VideoInfo
        val newItem = getNewItem(newItemPosition) as? VideoInfo
        return oldItem?.hashCode() == newItem?.hashCode()
    }

}
