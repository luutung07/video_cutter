package com.example.videocutter.presentation.adjust

import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.example.library_base.common.usecase.IViewListener
import com.example.library_base.eventbus.EventBusManager
import com.example.videocutter.R
import com.example.videocutter.common.event.DeleteVideoEvent
import com.example.videocutter.common.extensions.coroutinesLaunch
import com.example.videocutter.common.extensions.handleUiState
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.AdjustFragmentBinding
import com.example.videocutter.presentation.widget.recyclerview.COLLECTION_MODE
import com.example.videocutter.presentation.widget.video.VideoControlView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdjustFragment : VideoCutterFragment<AdjustFragmentBinding>(R.layout.adjust_fragment) {

    companion object {
        const val PATHS_KEY = "PATHS_KEY"
    }

    private val viewModel by viewModels<AdjustViewModel>()

    private val adapter by lazy { AdjustAdapter() }

    @UnstableApi
    override fun onInitView() {
        super.onInitView()
        setUpView()
        setUpAdapter()
    }

    override fun onStop() {
        super.onStop()
        binding.vcvAdjust.stop()
        viewModel.isStart = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.vcvAdjust.releasePlayer()
        removeListener()
    }

    override fun onBackPressedFragment(tag: String?) {
        EventBusManager.instance?.postPending(DeleteVideoEvent(viewModel.listVideoInfoSelected))

        super.onBackPressedFragment(tag)
    }

    override fun onObserverViewModel() {
        super.onObserverViewModel()
        coroutinesLaunch(viewModel.videoListState) {
            handleUiState(it, object : IViewListener {
                override fun onSuccess() {
                    if (it.data?.size == 1) { // item add
                        onBackPressedFragment()
                    } else {
                        binding.cvAdjust.submitList(it.data)
                    }
                }
            })
        }
    }

    @UnstableApi
    private fun setUpView() {
        binding.vcvAdjust.apply {
            setListPath(viewModel.listPath)
            start()
            setTimeLimeMax(viewModel.maxDuration)
            setOnLeftListener {
                viewModel.isStart = !viewModel.isStart
                if (viewModel.isStart) {
                    start()
                } else {
                    stop()
                }
            }

            listenerStateVideo = object : VideoControlView.IVideoControlCallback.IStateVideo {
                override fun onVideoEnd() {
                    viewModel.isStart = false
                }
            }
        }

        binding.hvAdjust.setActionLeft {
            onBackPressedFragment()
        }
    }

    @UnstableApi
    private fun setUpAdapter() {
        binding.cvAdjust.setAdapter(adapter)
        binding.cvAdjust.setLayoutManager(COLLECTION_MODE.HORIZONTAL)
        binding.cvAdjust.setDragRecyclerView(false) { oldIndex, newIndex ->
            viewModel.swapVideo(oldIndex, newIndex)
            binding.vcvAdjust.swapListPath(oldIndex, newIndex)
        }

        addListener()
    }

    @UnstableApi
    private fun addListener() {
        adapter.listener = object : AdjustAdapter.IAdjustCallBack {
            override fun onDelete(id: Long?, path: String) {
                viewModel.deleteVideo(id)
                binding.vcvAdjust.deletePath(path)
                binding.vcvAdjust.setTimeLimeMax(viewModel.maxDuration)
            }

            override fun onAddMore() {
                onBackPressedFragment()
            }
        }
    }

    private fun removeListener() {
        adapter.listener = null
    }
}
