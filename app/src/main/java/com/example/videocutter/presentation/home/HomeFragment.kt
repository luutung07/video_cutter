package com.example.videocutter.presentation.home

import android.os.Build
import android.util.Log
import com.example.baseapp.base.extension.checkBeforeBack
import com.example.baseapp.base.extension.setOnSafeClick
import com.example.library_base.common.BaseActivity
import com.example.videocutter.R
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.HomeFragmentBinding
import com.example.videocutter.domain.model.VideoInfo
import com.example.videocutter.presentation.widget.recyclerview.COLLECTION_MODE

class HomeFragment : VideoCutterFragment<HomeFragmentBinding>(R.layout.home_fragment) {

    private val adapter by lazy {
        HomeAdapter()
    }

    override fun onInitView() {
        super.onInitView()
        setUpAdapter()
        setUpView()
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

    private fun setUpView() {
        binding.llHomeStart.setOnSafeClick {
            navigateTo(R.id.fragmentSelect)
        }

        binding.trim.extractFrameFromVideo("/storage/emulated/0/Pictures/Screenshots/Record_2024-03-02-00-21-05_95efe893111dbaee1d6d49b1dae54f76.mp4")

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
