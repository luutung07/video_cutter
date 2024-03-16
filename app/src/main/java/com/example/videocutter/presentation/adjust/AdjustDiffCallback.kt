package com.example.videocutter.presentation.adjust

import com.example.library_base.adapter.BaseDiffUtilCallback
import com.example.videocutter.presentation.display.model.video.VideoInfoDisplay

class AdjustDiffCallback(oldList: List<Any>, newList: List<Any>) :
    BaseDiffUtilCallback<Any>(oldList, newList) {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = getOldItem(oldItemPosition)
        val newItem = getNewItem(newItemPosition)
        return when {
            oldItem is VideoInfoDisplay && newItem is VideoInfoDisplay -> {
                oldItem.getVideoInfo().id == newItem.getVideoInfo().id
            }
            else -> true
        }

    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = getOldItem(oldItemPosition)
        val newItem = getNewItem(newItemPosition)
        return when {
            oldItem is VideoInfoDisplay && newItem is VideoInfoDisplay -> {
                oldItem.getVideoInfo().hashCode() == newItem.getVideoInfo().hashCode()
            }
            else -> true
        }
    }

}
