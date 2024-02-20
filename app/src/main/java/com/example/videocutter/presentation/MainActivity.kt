package com.example.videocutter.presentation

import com.example.baseapp.base.extension.gone
import com.example.baseapp.base.extension.show
import com.example.videocutter.R
import com.example.videocutter.common.srceen.VideoCutterActivity
import com.example.videocutter.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : VideoCutterActivity<ActivityMainBinding>(R.layout.activity_main) {
    override fun showLoading(message: String) {
        super.showLoading(message)
        binding.flMainLoading.show()
    }

    override fun hideLoading() {
        super.hideLoading()
        binding.flMainLoading.gone()
    }
}
