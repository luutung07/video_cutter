package com.example.videocutter.presentation.music

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.example.baseapp.base.extension.getAppColor
import com.example.baseapp.base.extension.getAppString
import com.example.baseapp.base.extension.gone
import com.example.baseapp.base.extension.setOnSafeClick
import com.example.baseapp.base.extension.show
import com.example.library_base.adapter.BaseAdapter
import com.example.library_base.adapter.BaseVH
import com.example.library_base.extension.LONG_DEFAULT
import com.example.videocutter.R
import com.example.videocutter.common.extensions.convertTimeToString
import com.example.videocutter.databinding.MusicItemBinding
import com.example.videocutter.databinding.MusicNoneItemBinding
import com.example.videocutter.presentation.display.model.music.MUSIC_TYPE
import com.example.videocutter.presentation.display.model.music.MusicDisplay
import com.example.videocutter.presentation.widget.addmusic.AddMusicView

class AddMusicAdapter : BaseAdapter() {

    companion object {
        private const val NOPE_TYPE = 1
        private const val DATA_TYPE = 2

        const val TYPE_NOPE = "TYPE_NOPE"
    }

    var listener: IMusicCallBack? = null

    override fun getLayoutResource(viewType: Int): Int {
        return when (viewType) {
            NOPE_TYPE -> R.layout.music_none_item
            DATA_TYPE -> R.layout.music_item
            else -> INVALID_RESOURCE
        }
    }

    override fun getItemViewTypeCustom(position: Int): Int {
        val item = getDataAtPosition(position)
        return when (item) {
            is Pair<*, *> -> NOPE_TYPE
            is MusicDisplay -> DATA_TYPE
            else -> NORMAL_VIEW_TYPE
        }
    }

    override fun getDiffUtil(oldList: List<Any>, newList: List<Any>): DiffUtil.Callback {
        return MusicDiffCallback(oldList, newList)
    }

    override fun onCreateViewHolder(viewType: Int, binding: ViewDataBinding): BaseVH<*>? {
        return when (viewType) {
            NOPE_TYPE -> MusicNopeVH(binding as MusicNoneItemBinding)
            DATA_TYPE -> MusicVH(binding as MusicItemBinding)
            else -> null
        }
    }

    inner class MusicNopeVH(private val binding: MusicNoneItemBinding) :
        BaseVH<Pair<String, Boolean>>(binding) {

        init {
            binding.root.setOnSafeClick {
                val item = getDataAtPosition(adapterPosition) as? Pair<*, *>
                if (item != null) {
                    listener?.onSelectNone(item.second as Boolean)
                }
            }
        }

        override fun onBind(data: Pair<String, Boolean>, payloads: List<Any>) {
            super.onBind(data, payloads)
            (payloads.first() as? List<*>)?.forEach {
                when (it) {
                    UPDATE_STATE_SELECT_NONE_PAYLOAD -> {
                        if (data.second) {
                            binding.ivMusicNopeSelect.show()
                        } else {
                            binding.ivMusicNopeSelect.gone()
                        }
                    }
                }
            }
        }
    }

    inner class MusicVH(private val binding: MusicItemBinding) : BaseVH<MusicDisplay>(binding) {

        init {
            binding.root.setOnSafeClick {
                val item = getDataAtPosition(adapterPosition) as? MusicDisplay
                item?.let {
                    if (it.isDownLoaded == true) {
                        listener?.onSelectMusic(item.getMusic().id, it.getMusic().url)
                    } else {
                        listener?.onDownloadMusic(item.getMusic().id, it.getMusic().url)
                    }
                }
            }

            binding.root.setOnLongClickListener {
                val item = getDataAtPosition(adapterPosition) as? MusicDisplay
                item?.let {
                    if (!it.isShowTrimMusic) {
                        listener?.onShowCutMusic(it.getMusic().id, it.getMusic().url)
                    }
                }
                true
            }

            binding.ivMusicPlay.setOnSafeClick {
                val item = getDataAtPosition(adapterPosition) as? MusicDisplay
                item?.let {
                    listener?.onPlay(it.getMusic().id)
                }
            }

            binding.tvMusicSelect.setOnSafeClick {
                val item = getDataAtPosition(adapterPosition) as? MusicDisplay
                item?.let {
                    if (it.isSelect) {
                        listener?.onShowCutMusic(it.getMusic().id, it.getMusic().url)
                    } else {
                        listener?.onSelectMusic(item.getMusic().id, it.getMusic().url)
                    }
                }
            }
        }

        override fun onBind(data: MusicDisplay) {
            super.onBind(data)
            binding.tvMusicTime.text = data.getMusic().duration?.convertTimeToString()
            binding.tvMusicName.text = data.getMusic().name
            binding.amvMusic.setDuration(data.getMusic().duration ?: LONG_DEFAULT)
            checkTypeLocal(data)
            showCrop(data)
            statePlay(data)
            setStateSelect(data)
            checkStateDownload(data)
        }

        override fun onBind(data: MusicDisplay, payloads: List<Any>) {
            super.onBind(data, payloads)
            (payloads.first() as? List<*>)?.forEach {
                when (it) {
                    UPDATE_STATE_SHOW_CROP_PAYLOAD -> showCrop(data)

                    UPDATE_STATE_PLAY_PAYLOAD -> statePlay(data)

                    UPDATE_CURRENT_POSITION_PAYLOAD -> setCurrentPosition(data)

                    UPDATE_STATE_SELECT_MUSIC_PAYLOAD -> setStateSelect(data)

                    UPDATE_STATE_DOWNLOAD_PAYLOAD -> checkStateDownload(data)
                }
            }
        }

        private fun checkStateDownload(data: MusicDisplay) {
            if (data.isDownLoaded == true) {
                binding.ivMusicSelect.setImageResource(R.drawable.ic_select_music)
            } else {
                binding.ivMusicSelect.setImageResource(R.drawable.ic_download)
            }
        }

        private fun setCurrentPosition(data: MusicDisplay) {
            binding.amvMusic.setCurrentDuration(data.currentPosition ?: LONG_DEFAULT)
        }

        private fun setStateSelect(data: MusicDisplay) {
            if (data.isSelect) {
                binding.ivMusicSelect.show()
            } else {
                binding.ivMusicSelect.gone()
            }
        }

        private fun statePlay(data: MusicDisplay) {
            if (data.isPlay) {
                binding.ivMusicPlay.setImageResource(R.drawable.ic_btn_play)
            } else {
                binding.ivMusicPlay.setImageResource(R.drawable.ic_btn_pause)
            }
        }

        private fun showCropDownloaded(data: MusicDisplay) {
            if (data.isSelect) {
                binding.tvMusicSelect.text = getAppString(R.string.done)
            } else {
                binding.tvMusicSelect.text = getAppString(R.string.add)
            }
        }

        private fun showCrop(data: MusicDisplay) {
            if (data.isShowTrimMusic) {
                addListenerCrop(data.getMusic().id)
                binding.amvMusic.reset()

                binding.clMusicSelectMusic.show()
                binding.clMusicRoot.setBackgroundColor(getAppColor(R.color.gray_comment))

                binding.ivMusicCutSound.show()
                binding.tvMusicTime.gone()

                if (data.isDownLoaded == true) {
                    binding.ivMusicSelect.gone()
                    binding.tvMusicSelect.show()
                    showCropDownloaded(data)
                } else {
                    binding.ivMusicSelect.setImageResource(R.drawable.ic_download)
                    binding.tvMusicSelect.gone()
                }

            } else {
                removeListenerCrop()

                binding.clMusicRoot.background = null
                binding.clMusicSelectMusic.gone()

                binding.ivMusicCutSound.gone()
                binding.tvMusicTime.show()

                setStateSelect(data)
                binding.tvMusicSelect.gone()
            }
        }

        private fun addListenerCrop(id: String?) {
            binding.amvMusic.listener = object : AddMusicView.IAddMusicCallBack {
                override fun onCallBack(start: Float, end: Float) {
                    listener?.onTimeLine(start.toLong(), end.toLong(), id)
                }

                override fun onHasCrop(isCrop: Boolean) {
                    listener?.onCropVideo(isCrop)
                }
            }
        }

        private fun removeListenerCrop() {
            binding.amvMusic.listener = null
        }

        private fun checkTypeLocal(data: MusicDisplay) {
            if (data.type == MUSIC_TYPE.ITUNES) {
                binding.ivMusicSelect.show()
            } else {
                binding.ivMusicSelect.gone()
            }
        }
    }

    interface IMusicCallBack {
        fun onSelectMusic(id: String?, url: String?)
        fun onSelectNone(isSelect: Boolean)
        fun onDownloadMusic(id: String?, url: String?)
        fun onShowCutMusic(id: String?, url: String?)
        fun onCropVideo(isCrop: Boolean)
        fun onTimeLine(start: Long, end: Long, id: String?)
        fun onPlay(id: String?)
    }
}
