package com.example.videocutter.presentation.home

import com.example.baseapp.base.extension.setOnSafeClick
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
    }
}
