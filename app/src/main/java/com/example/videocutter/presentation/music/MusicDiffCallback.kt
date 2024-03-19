package com.example.videocutter.presentation.music

import com.example.library_base.adapter.BaseDiffUtilCallback
import com.example.videocutter.presentation.display.model.music.MusicDisplay

const val UPDATE_STATE_SELECT_NONE_PAYLOAD = "UPDATE_STATE_SELECT_NONE_PAYLOAD"
const val UPDATE_STATE_SELECT_MUSIC_PAYLOAD = "UPDATE_STATE_SELECT_MUSIC_PAYLOAD"
const val UPDATE_STATE_SHOW_CROP_PAYLOAD = "UPDATE_STATE_SHOW_CROP_PAYLOAD"
const val UPDATE_STATE_PLAY_PAYLOAD = "UPDATE_STATE_PLAY_PAYLOAD"
const val UPDATE_CURRENT_POSITION_PAYLOAD = "UPDATE_CURRENT_POSITION_PAYLOAD"
const val UPDATE_STATE_DOWNLOAD_PAYLOAD = "UPDATE_STATE_DOWNLOAD_PAYLOAD"

class MusicDiffCallback(oldList: List<Any>, newList: List<Any>) :
    BaseDiffUtilCallback<Any>(oldList, newList) {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = getOldItem(oldItemPosition)
        val newItem = getNewItem(newItemPosition)
        return when {
            oldItem is MusicDisplay && newItem is MusicDisplay -> {
                oldItem.type == newItem.type && oldItem.getMusic().id == newItem.getMusic().id
            }

            oldItem is Pair<*, *> && newItem is Pair<*, *> -> {
                oldItem.first == newItem.first
            }

            else -> false
        }

    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = getOldItem(oldItemPosition)
        val newItem = getNewItem(newItemPosition)
        return when {
            oldItem is MusicDisplay && newItem is MusicDisplay -> {
                val oldState = oldItem.getState()
                val newState = newItem.getState()
                oldState.isSelect == newState.isSelect &&
                        oldState.isShowTrimMusic == newState.isShowTrimMusic &&
                        oldState.isDownLoaded == newState.isDownLoaded &&
                        oldState.isPlay == newState.isPlay &&
                        oldState.currentPosition == newState.currentPosition &&
                        oldState.start == newState.start &&
                        oldState.end == newState.end
            }

            oldItem is Pair<*, *> && newItem is Pair<*, *> -> {
                oldItem.second == newItem.second
            }

            else -> true
        }
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val list: MutableList<Any> = arrayListOf()
        val oldItem = getOldItem(oldItemPosition)
        val newItem = getNewItem(newItemPosition)

        when {
            oldItem is MusicDisplay && newItem is MusicDisplay -> {

                val oldState = oldItem.getState()
                val newState = newItem.getState()

                if (oldState.isShowTrimMusic != newState.isShowTrimMusic) {
                    list.add(UPDATE_STATE_SHOW_CROP_PAYLOAD)
                }
                if (oldState.isPlay != newState.isPlay) {
                    list.add(UPDATE_STATE_PLAY_PAYLOAD)
                }
                if (oldState.currentPosition != newState.currentPosition || oldState.start != newState.start || oldState.end == newState.end) {
                    list.add(UPDATE_CURRENT_POSITION_PAYLOAD)
                }
                if (oldState.isSelect != newState.isSelect) {
                    list.add(UPDATE_STATE_SELECT_MUSIC_PAYLOAD)
                }
                if (oldState.isDownLoaded != newState.isDownLoaded) {
                    list.add(UPDATE_STATE_DOWNLOAD_PAYLOAD)
                }
            }

            oldItem is Pair<*, *> && newItem is Pair<*, *> -> {
                if (oldItem.second != newItem.second) {
                    list.add(UPDATE_STATE_SELECT_NONE_PAYLOAD)
                }
            }
        }
        return list.ifEmpty { null }
    }
}
