package com.example.videocutter.presentation.select.file

import com.example.library_base.adapter.BaseDiffUtilCallback
import com.example.videocutter.presentation.repodisplay.model.video.VideoInfoDisplay

const val UPDATE_STATE_SELECT_FILE_PAYLOAD = "UPDATE_STATE_SELECT_FILE_PAYLOAD"

class FileDiffCallback(oldList: List<Any>, newList: List<Any>) :
    BaseDiffUtilCallback<Any>(oldList, newList) {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = getOldItem(oldItemPosition) as? VideoInfoDisplay
        val newItem = getNewItem(newItemPosition) as? VideoInfoDisplay
        return oldItem?.getVideoInfo()?.id == newItem?.getVideoInfo()?.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = getOldItem(oldItemPosition) as? VideoInfoDisplay
        val newItem = getNewItem(newItemPosition) as? VideoInfoDisplay
        return oldItem?.isSelect == newItem?.isSelect
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val list: MutableList<Any> = arrayListOf()
        val oldItem = getOldItem(oldItemPosition) as? VideoInfoDisplay
        val newItem = getNewItem(newItemPosition) as? VideoInfoDisplay

        if (oldItem?.isSelect != newItem?.isSelect) {
            list.add(UPDATE_STATE_SELECT_FILE_PAYLOAD)
        }

        return list.ifEmpty { null }
    }

}
