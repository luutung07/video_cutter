package com.example.library_base.common.binding

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.library_base.common.BaseActivity
import com.example.library_base.common.BaseView


abstract class BaseBindingActivity<DB : ViewDataBinding>(layoutId: Int) : BaseActivity(layoutId),
    BaseView {

    protected val binding
        get() = _binding!!
    private var _binding: DB? = null

    protected var initSetFullScreen = false

    init {

    }

    //region lifecycle
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onInitView() {
        super.onInitView()
        setFullScreen()
    }

    override fun attachView() {
        _binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
    }
    //endregion

    private fun setFullScreen() {
        window?.apply {
            WindowCompat.setDecorFitsSystemWindows(this, false)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v: View, windowInsets: WindowInsetsCompat ->
            if (initSetFullScreen) {
                // fix lỗi navigation bar chèn lên recyclerview khi set full screen
                // fix lỗi người dùng vào app sau đó ấn Home để cài đặt show navigation bar
                val navigationBarInset = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
                v.updatePadding(0, navigationBarInset.top, 0, navigationBarInset.bottom)
            } else {
                val systemBarInset = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.updatePadding(0, systemBarInset.top, 0, systemBarInset.bottom)
            }
            windowInsets
        }
    }

    fun setNormalScreen() {
        window?.apply {
            WindowCompat.setDecorFitsSystemWindows(this, true)
            //reset view
            (binding.root.layoutParams as ViewGroup.MarginLayoutParams).setMargins(0, 0, 0, 0)
            binding.root.requestLayout()
        }
    }
}
