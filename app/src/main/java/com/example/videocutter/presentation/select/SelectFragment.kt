package com.example.videocutter.presentation.select

import androidx.fragment.app.viewModels
import com.example.baseapp.base.extension.getAppDrawable
import com.example.baseapp.base.extension.getAppString
import com.example.baseapp.base.extension.gone
import com.example.baseapp.base.extension.show
import com.example.library_base.common.usecase.IViewListener
import com.example.library_base.eventbus.EventBusManager
import com.example.library_base.eventbus.IEvent
import com.example.videocutter.R
import com.example.videocutter.common.event.SelectFolderEvent
import com.example.videocutter.common.extensions.coroutinesLaunch
import com.example.videocutter.common.extensions.handleUiState
import com.example.videocutter.common.loader.aim.SLIDE_TYPE
import com.example.videocutter.common.loader.aim.SlideAnimation
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.SelectFragmentBinding
import com.example.videocutter.domain.model.VideoInfo
import com.example.videocutter.presentation.select.file.FileFragment
import com.example.videocutter.presentation.select.folder.FolderFragment
import com.example.videocutter.presentation.widget.recyclerview.COLLECTION_MODE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectFragment : VideoCutterFragment<SelectFragmentBinding>(R.layout.select_fragment) {

    private val viewModel by viewModels<SelectViewModel>()

    private val adapter by lazy { SelectedAdapter() }

    override fun getContainerId() = R.id.flSelectContainer

    override fun onInitView() {
        super.onInitView()
        setUpView()
    }

    override fun onEvent(event: IEvent) {
        super.onEvent(event)
        when (event) {
            is SelectFolderEvent -> {
                viewModel.isOpenFile = true
                binding.hvSelect.setLabelCenter(event.name)
                setUpFile()
                EventBusManager.instance?.removeSticky(event)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBusManager.instance?.register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBusManager.instance?.unregister(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeListener()
    }

    override fun onObserverViewModel() {
        super.onObserverViewModel()
        coroutinesLaunch(viewModel.selectFileState) {
            handleUiState(it, object : IViewListener {
                override fun onSuccess() {
                    if (it.data.isNullOrEmpty()) {
                        binding.mcvSelectItem.gone()
                    } else {
                        binding.mcvSelectItem.show()
                        binding.tvSelect.text = getAppString(R.string.add_item, it.data!!.size)
                        binding.cvSelect.post {
                            binding.cvSelect.scrollToPosition(it.data!!.lastIndex)
                        }
                    }
                    binding.cvSelect.submitList(it.data)
                }
            })
        }
    }

    override fun onBackPressedFragment(tag: String?) {
        if (viewModel.isOpenFile) {
            setUpFolder()
            viewModel.isOpenFile = false
        } else super.onBackPressedFragment(tag)
    }

    private fun setUpView() {

        replaceFragmentInsideFragment(FileFragment(), keepToBackStack = false)

        binding.hvSelect.apply {
            setActionLeft {
                onBackPressedFragment()
            }
            setActionCenter {
                viewModel.isOpenFile = !viewModel.isOpenFile
                if (viewModel.isOpenFile) {
                    setUpFile()
                } else {
                    setUpFolder()
                }
            }
        }

        setUpAdapter()
    }

    private fun setUpFolder() {
        binding.hvSelect.setIcCenter(getAppDrawable(R.drawable.ic_arrow_down))
        replaceFragmentInsideFragment(
            FolderFragment(), keepToBackStack = false,
            screenAnim = SlideAnimation(SLIDE_TYPE.BOTTOM_TO_TOP)
        )
    }

    private fun setUpFile() {
        binding.hvSelect.setIcCenter(getAppDrawable(R.drawable.ic_arrow_up))
        replaceFragmentInsideFragment(
            FileFragment(),
            keepToBackStack = false,
            screenAnim = SlideAnimation(SLIDE_TYPE.BOTTOM_TO_TOP)
        )
    }

    private fun setUpAdapter() {
        binding.cvSelect.setAdapter(adapter)
        binding.cvSelect.setLayoutManager(COLLECTION_MODE.HORIZONTAL)
        binding.cvSelect.setDragRecyclerView(swap = { oldIndex, newIndex ->
            viewModel.swap(oldIndex, newIndex)
        })
        addListener()
    }

    private fun addListener() {
        adapter.listener = object : SelectedAdapter.ISelectCallBack {
            override fun onDelete(id: Long?) {
                viewModel.selectFile(id)
            }
        }
    }

    private fun removeListener() {
        adapter.listener = null
    }
}
