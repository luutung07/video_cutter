package com.example.videocutter.presentation.editvideo.filter

import androidx.fragment.app.viewModels
import com.example.library_base.common.usecase.IViewListener
import com.example.videocutter.R
import com.example.videocutter.common.extensions.coroutinesLaunch
import com.example.videocutter.common.extensions.handleUiState
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.FilterFragmentBinding
import com.example.videocutter.presentation.editvideo.EditVideoFragment
import com.example.videocutter.presentation.editvideo.EditVideoViewModel
import com.example.videocutter.presentation.repodisplay.model.FILTER_TYPE
import com.example.videocutter.presentation.widget.recyclerview.COLLECTION_MODE

class FilterFragment : VideoCutterFragment<FilterFragmentBinding>(R.layout.filter_fragment) {

    private val viewModel by viewModels<EditVideoViewModel>(ownerProducer = { requireParentFragment() })

    private val adapter by lazy { FilterAdapter() }

    private val parentRoot by lazy {
        parentFragment as EditVideoFragment
    }

    override fun onInitView() {
        super.onInitView()
        setEventView()
        setUpAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeListener()
    }

    override fun onObserverViewModel() {
        super.onObserverViewModel()
        coroutinesLaunch(viewModel.listFilterState) {
            handleUiState(it, object : IViewListener {
                override fun onSuccess() {
                    binding.cvFilter.submitList(it.data)
                }
            })
        }
    }

    override fun onBackPressedFragment() {
        parentFragmentManager.popBackStack()
    }

    private fun setEventView() {
        binding.hvFilterFooter.apply {
            setActionLeft {
                if (viewModel.filterType != FILTER_TYPE.ORIGINAL) {
                    onBackPressedFragment()
                } else {
                    viewModel.filterType = FILTER_TYPE.ORIGINAL
                    parentRoot.setColor(viewModel.filterType)
                }
            }

            setActionRight {
                onBackPressedFragment()
            }
        }
    }

    private fun setUpAdapter() {
        binding.cvFilter.setAdapter(adapter)
        binding.cvFilter.setLayoutManager(COLLECTION_MODE.HORIZONTAL)

        addListener()
    }

    private fun addListener() {
        adapter.listener = object : FilterAdapter.IFilterCallBack {
            override fun onSelectFilter(type: FILTER_TYPE) {
                viewModel.filterType = type
                viewModel.getListFilter()
                parentRoot.setColor(viewModel.filterType)
            }
        }
    }

    private fun removeListener() {
        adapter.listener = null
    }
}
