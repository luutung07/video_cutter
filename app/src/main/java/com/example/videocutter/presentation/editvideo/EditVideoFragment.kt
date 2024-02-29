package com.example.videocutter.presentation.editvideo

import androidx.fragment.app.viewModels
import androidx.media3.common.util.UnstableApi
import com.example.baseapp.base.extension.hide
import com.example.baseapp.base.extension.show
import com.example.library_base.common.usecase.IViewListener
import com.example.videocutter.R
import com.example.videocutter.common.extensions.convertTimeToString
import com.example.videocutter.common.extensions.coroutinesLaunch
import com.example.videocutter.common.extensions.handleUiState
import com.example.videocutter.common.loader.aim.SlideAnimation
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.EditVideoFragmentBinding
import com.example.videocutter.presentation.editvideo.crop.CropVideoFragment
import com.example.videocutter.presentation.editvideo.rotate.RotateFragment
import com.example.videocutter.presentation.repodisplay.model.FEATURE_TYPE
import com.example.videocutter.presentation.widget.crop.CROP_TYPE
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

    override fun getContainerId() = R.id.flEditVideoFeature

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
        removeListener()
    }

    override fun onObserverViewModel() {
        super.onObserverViewModel()
        coroutinesLaunch(viewModel.listFeatureState) {
            handleUiState(it, object : IViewListener {
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
        addListener()
    }

    private fun addListener() {
        adapter.listener = object : FeatureAdapter.IFeatureCallback {
            override fun onFeature(type: FEATURE_TYPE) {
                binding.flEditVideoFeature.show()
                showHeader(false)
                when (type) {
                    FEATURE_TYPE.CROP -> {
                        viewModel.getListCrop()
                        setTypeCrop(CROP_TYPE.TYPE_CUSTOM)
                        addFragmentInsideFragment(
                            CropVideoFragment(),
                            screenAnim = SlideAnimation()
                        )
                    }

                    FEATURE_TYPE.ROTATE -> {
                        addFragmentInsideFragment(
                            RotateFragment(),
                            screenAnim = SlideAnimation()
                        )
                    }

                    else -> {}
                }
            }
        }
    }

    private fun removeListener() {
        binding.vcvEditVideo.listenerStateVideo = null
        adapter.listener = null
    }

    fun showHeader(isShow: Boolean) {
        if (isShow) {
            binding.hvEditVideo.show()
        } else {
            binding.hvEditVideo.hide()
        }
    }

    fun setTypeCrop(cropType: CROP_TYPE) {
        binding.vcvEditVideo.setTypeCrop(cropType)
    }

    fun hideCrop() {
        binding.vcvEditVideo.hideTypeCrop()
    }

    fun hasInputUser(hasInput: Boolean) {
        binding.vcvEditVideo.setHasInputUser(hasInput)
    }

    fun rotateVideo(rotate: Float){
        binding.vcvEditVideo.setRotate(rotate)
    }
}
