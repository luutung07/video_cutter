package com.example.videocutter.presentation.editvideo

import androidx.fragment.app.viewModels
import androidx.media3.common.util.UnstableApi
import com.example.library_base.common.usecase.IViewListener
import com.example.videocutter.R
import com.example.videocutter.common.extensions.convertTimeToString
import com.example.videocutter.common.extensions.coroutinesLaunch
import com.example.videocutter.common.extensions.handleUiState
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.EditVideoFragmentBinding
import com.example.videocutter.presentation.widget.recyclerview.COLLECTION_MODE
import com.example.videocutter.presentation.widget.video.VideoControlView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditVideoFragment :
    VideoCutterFragment<EditVideoFragmentBinding>(R.layout.edit_video_fragment) {

    companion object {
        const val VIDEO_INFO_EDIT_KEY = "VIDEO_INFO_EDIT_KEY"
    }

    private val viewModel by viewModels<EditVideoViewModel>()

    private val adapter by lazy { FeatureAdapter() }

    private var isPlay = true

    @UnstableApi
    override fun onInitView() {
        super.onInitView()
        showLoading()
        setUpView()
        setUpAdapter()
    }

    override fun onStop() {
        super.onStop()
        binding.vcvEditVideo.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.vcvEditVideo.releasePlayer()
        binding.vcvEditVideo.listenerStateVideo = null
    }

    override fun onObserverViewModel() {
        super.onObserverViewModel()
        coroutinesLaunch(viewModel.listFeatureState){
            handleUiState(it, object : IViewListener{
                override fun onSuccess() {
                    binding.cvEditVideoFuture.submitList(it.data)
                }
            })
        }
    }

    @UnstableApi
    private fun setUpView() {
        binding.hvEditVideo.apply {
            setActionLeft {
                onBackPressedFragment()
            }
        }

        binding.vcvEditVideo.apply {
            setTimeCenter(viewModel.maxDuration.convertTimeToString())
            setListPath(viewModel.listPath, hasExtract = true, hasTimeStart = false)
            hasTimeStart(false)
            listenerStateVideo = object : VideoControlView.IVideoControlCallback.IStateVideo {

                override fun onVideoEnd() {
                    isPlay = false
                }

                override fun onInitVideoSuccess() {
                    super.onInitVideoSuccess()
                    hideLoading()
                }
            }
            setOnLeftListener {
                isPlay = !isPlay
                if (isPlay) {
                    start()
                } else {
                    stop()
                }
            }
        }
    }

    private fun setUpAdapter() {
        binding.cvEditVideoFuture.setAdapter(adapter)
        binding.cvEditVideoFuture.setLayoutManager(COLLECTION_MODE.HORIZONTAL)
    }
}
