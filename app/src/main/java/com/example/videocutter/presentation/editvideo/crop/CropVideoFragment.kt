package com.example.videocutter.presentation.editvideo.crop

import androidx.fragment.app.viewModels
import com.example.library_base.common.usecase.IViewListener
import com.example.videocutter.R
import com.example.videocutter.common.extensions.coroutinesLaunch
import com.example.videocutter.common.extensions.handleUiState
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.CropVideoFragmentBinding
import com.example.videocutter.presentation.editvideo.EditVideoFragment
import com.example.videocutter.presentation.editvideo.EditVideoViewModel
import com.example.videocutter.presentation.widget.crop.CROP_TYPE
import com.example.videocutter.presentation.widget.recyclerview.COLLECTION_MODE

class CropVideoFragment :
    VideoCutterFragment<CropVideoFragmentBinding>(R.layout.crop_video_fragment) {

    private val viewModel by viewModels<EditVideoViewModel>(ownerProducer = { requireParentFragment() })

    private val adapter by lazy { CropAdapter() }

    private val parentRoot by lazy {
        (parentFragment) as EditVideoFragment
    }

    override fun onPrepareInitView() {
        super.onPrepareInitView()
        parentRoot.hasInputUser(true)
    }

    override fun onInitView() {
        super.onInitView()
        setUpAdapter()
        setEventView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeListener()
    }

    override fun onObserverViewModel() {
        super.onObserverViewModel()
        coroutinesLaunch(viewModel.listCropState) {
            handleUiState(it, object : IViewListener {
                override fun onSuccess() {
                    binding.cvCropVideo.submitList(it.data)
                }
            })
        }
    }

    override fun onBackPressedFragment() {
        parentRoot.showHeader(true)
        parentFragmentManager.popBackStack()
    }

    private fun setEventView() {
        binding.hvCropVideoFooter.apply {
            setActionLeft {
                parentRoot.hideCrop()
                onBackPressedFragment()
            }

            setActionRight {
                parentRoot.hasInputUser(false)
                onBackPressedFragment()
            }
        }
    }

    private fun setUpAdapter() {
        binding.cvCropVideo.setAdapter(adapter)
        binding.cvCropVideo.setLayoutManager(COLLECTION_MODE.HORIZONTAL)
        addListener()
    }

    private fun addListener() {
        adapter.listener = object : CropAdapter.ICropCallBack {
            override fun onCropType(type: CROP_TYPE) {
                viewModel.cropType = type
                viewModel.getListCrop()
                parentRoot.setTypeCrop(type)
            }
        }
    }

    private fun removeListener() {
        adapter.listener = null
    }
}
