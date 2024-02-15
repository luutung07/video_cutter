package com.example.videocutter.presentation.select.folder

import androidx.fragment.app.viewModels
import com.example.library_base.common.usecase.IViewListener
import com.example.library_base.eventbus.EventBusManager
import com.example.videocutter.R
import com.example.videocutter.common.event.OnBackPressFile
import com.example.videocutter.common.event.SelectFolderEvent
import com.example.videocutter.common.extensions.coroutinesLaunch
import com.example.videocutter.common.extensions.handleUiState
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.FolderFragmentBinding
import com.example.videocutter.presentation.select.SelectViewModel
import dagger.hilt.android.AndroidEntryPoint

class FolderFragment : VideoCutterFragment<FolderFragmentBinding>(R.layout.folder_fragment) {

    private val viewModel by viewModels<SelectViewModel>(ownerProducer = { requireParentFragment() })

    private val adapter by lazy { FolderAdapter() }

    override fun onInitView() {
        super.onInitView()
        setUpAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeListener()
    }

    private fun setUpAdapter() {
        binding.cvFolder.setAdapter(adapter)
        addListener()
    }

    override fun onBackPressedFragment(tag: String?) {
        EventBusManager.instance?.postPending(OnBackPressFile(true))
    }

    private fun addListener() {
        adapter.listener = object : FolderAdapter.IFolderCallBack {
            override fun onSelect(id: Long?,name: String) {
                viewModel.selectFolder(id)
                EventBusManager.instance?.postPending(SelectFolderEvent(id,name))
            }
        }
    }

    private fun removeListener() {
        adapter.listener = null
    }

    override fun onObserverViewModel() {
        super.onObserverViewModel()
        coroutinesLaunch(viewModel.folderState) {
            handleUiState(it, listener = object : IViewListener {
                override fun onSuccess() {
                    binding.cvFolder.submitList(it.data)
                }
            }, canShowLoading = true)
        }
    }
}
