package com.example.videocutter.presentation.editvideo

import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.media3.common.util.UnstableApi
import com.example.baseapp.base.extension.getAppColor
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
import com.example.videocutter.presentation.editvideo.filter.FilterFragment
import com.example.videocutter.presentation.editvideo.rotate.RotateFragment
import com.example.videocutter.presentation.editvideo.speed.SpeedFragment
import com.example.videocutter.presentation.display.model.editvideo.FEATURE_TYPE
import com.example.videocutter.presentation.display.model.editvideo.FILTER_TYPE
import com.example.videocutter.presentation.editvideo.cut.CutFragment
import com.example.videocutter.presentation.widget.crop.CROP_TYPE
import com.example.videocutter.presentation.widget.recyclerview.COLLECTION_MODE
import com.example.videocutter.presentation.widget.speedvideo.SPEED_TYPE
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
            setListPath(
                viewModel.listPath,
                listFrameDetach = mainViewModel.listFrameDetach.value.data,
                hasTimeStart = false
            )
            hasTimeStart(false)
            listenerStateVideo = object : VideoControlView.IVideoControlCallback.IStateVideo {

                override fun onVideoEnd() {
                    isPlay = false
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

                    FEATURE_TYPE.SPEED -> {
                        addFragmentInsideFragment(
                            SpeedFragment(),
                            screenAnim = SlideAnimation()
                        )
                    }

                    FEATURE_TYPE.FILTER -> {
                        viewModel.getListFilter()
                        addFragmentInsideFragment(
                            FilterFragment(),
                            screenAnim = SlideAnimation()
                        )
                    }

                    FEATURE_TYPE.MUSIC -> {
                        navigateTo(R.id.addMusicFragment)
                    }

                    FEATURE_TYPE.CUT -> {
                        navigateTo(R.id.cutFragment, bundleOf(
                            CutFragment.LIST_PATH_KEY to viewModel.listPath,
                            CutFragment.DURATION_KEY to viewModel.maxDuration
                        ))
                    }

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

    fun rotateVideo(rotate: Float) {
        binding.vcvEditVideo.setRotate(rotate)
    }

    fun setFlipVertical() {
        binding.vcvEditVideo.setFlipVertical()
    }

    fun setFlipHorizontal() {
        binding.vcvEditVideo.setFlipHorizontal()
    }

    fun setSpeed(type: SPEED_TYPE) {
        binding.vcvEditVideo.setSpeed(type.value)
    }

    fun setColor(type: FILTER_TYPE) {
        val color = when (type) {
            FILTER_TYPE.ORIGINAL -> null
            FILTER_TYPE.SUMMER -> getAppColor(R.color.summer)
            FILTER_TYPE.SPRING -> getAppColor(R.color.spring)
            FILTER_TYPE.FALL -> getAppColor(R.color.fall)
            FILTER_TYPE.WINTER -> getAppColor(R.color.winter)
        }
        binding.vcvEditVideo.setFilter(color)
    }
}
