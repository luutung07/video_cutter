package com.example.videocutter.presentation.home

import android.os.Build
import android.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.baseapp.base.extension.checkBeforeBack
import com.example.baseapp.base.extension.setOnSafeClick
import com.example.baseapp.base.extension.show
import com.example.library_base.common.BaseActivity
import com.example.library_base.common.usecase.IViewListener
import com.example.videocutter.R
import com.example.videocutter.common.extensions.coroutinesLaunch
import com.example.videocutter.common.extensions.handleUiState
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.HomeFragmentBinding
import com.example.videocutter.domain.model.VideoInfo
import com.example.videocutter.presentation.widget.recyclerview.COLLECTION_MODE

class HomeFragment : VideoCutterFragment<HomeFragmentBinding>(R.layout.home_fragment) {

    private val adapter by lazy {
        HomeAdapter()
    }

    @UnstableApi
    override fun onInitView() {
        super.onInitView()
        setUpAdapter()
        setUpView()
    }

    override fun onObserverViewModel() {
        super.onObserverViewModel()
        coroutinesLaunch(mainViewModel.listFrameDetach){
            handleUiState(it, object : IViewListener{
                override fun onSuccess() {
                    binding.clTrim.show()
                    val list = it.data?.map {
                        it.bitmap!!
                    }
                    list?.let { it1 -> binding.trim.setFrame(it1) }
                }
            })
        }
    }

    private fun setUpAdapter() {
        binding.cvHome.setAdapter(adapter)
        binding.cvHome.setLayoutManager(mode = COLLECTION_MODE.HORIZONTAL)

        val list = ArrayList<VideoInfo>()
        list.add(VideoInfo())
        list.add(VideoInfo())
        list.add(VideoInfo())
        list.add(VideoInfo())
        list.add(VideoInfo())
        list.add(VideoInfo())
        list.add(VideoInfo())
        binding.cvHome.submitList(list)
    }

    @UnstableApi
    private fun setUpView() {
        binding.llHomeStart.setOnSafeClick {
//            navigateTo(R.id.fragmentSelect)
            mainViewModel.detachFrameVideo(
                mutableListOf("/storage/emulated/0/DCIM/Camera/16fc0f5278ba735bfd37a88dc8be6866.mp4")
            )
        }

        binding.ivHomeSetting.setOnSafeClick {
            navigateTo(R.id.settingFragment)
        }

        requestPermission()
    }

    private fun requestPermission() {
        val listPermission: MutableList<String> = arrayListOf()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listPermission.add(android.Manifest.permission.READ_MEDIA_VIDEO)
        }
        listPermission.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        listPermission.add(android.Manifest.permission.RECORD_AUDIO)
        doRequestPermission(
            listPermission.toTypedArray(),
            object : BaseActivity.PermissionListener {
                override fun onAllow() {

                }

                override fun onDenied(neverAskAgainPermissionList: List<String>) {
                    neverAskAgainPermissionList.forEach {
                        Log.d(TAG, "onDenied: $it")
                    }
                }
            })
    }
}
