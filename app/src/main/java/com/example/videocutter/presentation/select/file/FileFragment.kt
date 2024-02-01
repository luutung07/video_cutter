package com.example.videocutter.presentation.select.file

import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.example.baseapp.base.extension.getAppString
import com.example.library_base.common.usecase.IViewListener
import com.example.videocutter.AppConfig
import com.example.videocutter.R
import com.example.videocutter.common.extensions.coroutinesLaunch
import com.example.videocutter.common.extensions.handleUiState
import com.example.videocutter.common.loader.aim.SLIDE_TYPE
import com.example.videocutter.common.loader.aim.SlideAnimation
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.FileFragmentBinding
import com.example.videocutter.presentation.select.SelectViewModel
import com.example.videocutter.presentation.select.folder.FolderAdapter
import com.example.videocutter.presentation.select.preview.PreviewFileFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FileFragment : VideoCutterFragment<FileFragmentBinding>(R.layout.file_fragment) {

    private val viewModel by viewModels<SelectViewModel>(ownerProducer = { requireParentFragment() })

    private val adapter by lazy { FileAdapter() }

    override fun getContainerId() = R.id.clFileRoot

    override fun onInitView() {
        super.onInitView()
        setUpAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeListener()
    }

    override fun onObserverViewModel() {
        super.onObserverViewModel()
        coroutinesLaunch(viewModel.fileState) {
            handleUiState(it, object : IViewListener {
                override fun onSuccess() {
                    binding.cvFile.submitList(it.data, viewModel.dataPage.hasLoadMore())
                }
            })
        }
    }

    private fun setUpAdapter() {
        binding.cvFile.setAdapter(adapter)
        binding.cvFile.apply {
            setMaxItemHorizontal(4)
            setLoadMoreListener {
                viewModel.getFiles()
            }
        }
        addListener()
    }

    private fun addListener() {
        adapter.listener = object : FileAdapter.IFileListener {
            override fun onSelect(id: Long?) {
                viewModel.selectFile(id)
            }

            override fun onMaxSelect() {
                showError(getAppString(R.string.max_item_select, AppConfig.MAX_ITEM_SELECT))
            }

            override fun onPReview(path: String) {
                replaceFragmentInsideFragment(
                    PreviewFileFragment(),
                    bundleOf(PreviewFileFragment.URL_PATH_KEY to path),
                    screenAnim = SlideAnimation(SLIDE_TYPE.BOTTOM_TO_TOP)
                )
            }
        }
    }

    private fun removeListener() {
        adapter.listener = null
    }
}
