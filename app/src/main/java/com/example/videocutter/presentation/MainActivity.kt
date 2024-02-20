package com.example.videocutter.presentation

import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.baseapp.base.extension.gone
import com.example.baseapp.base.extension.hide
import com.example.baseapp.base.extension.setOnSafeClick
import com.example.baseapp.base.extension.show
import com.example.baseapp.base.extension.toast
import com.example.library_base.common.binding.BaseBindingActivity
import com.example.videocutter.R
import com.example.videocutter.common.srceen.VideoCutterActivity
import com.example.videocutter.data.repo.VideoRepoImpl
import com.example.videocutter.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : VideoCutterActivity<ActivityMainBinding>(R.layout.activity_main) {
    override fun showLoading(message: String) {
        super.showLoading(message)
        binding.pbMainLoading.show()
    }

    override fun hideLoading() {
        super.hideLoading()
        binding.pbMainLoading.gone()
    }

    override fun onBackPressed() {
        try {
            Log.d(TAG, "backScreen: ${findNavController(R.id.nav_host_fragment).currentDestination}")
            findNavController(R.id.nav_host_fragment).navigateUp()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            Log.e(TAG, "backScreen: $e")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "backScreen: $e")
        }
    }
}
