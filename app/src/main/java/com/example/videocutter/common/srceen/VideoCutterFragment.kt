package com.example.videocutter.common.srceen

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.library_base.common.binding.BaseBindingFragment
import com.example.library_base.eventbus.IEvent
import com.example.library_base.eventbus.IEventHandler
import com.example.videocutter.common.extensions.getUpNavOptions
import com.example.videocutter.presentation.MainActivity
import com.example.videocutter.presentation.widget.headeralert.HEADER_ALERT_TIME_SHOWN
import com.example.videocutter.presentation.widget.headeralert.HEADER_ALERT_TYPE
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

open class VideoCutterFragment<DB : ViewDataBinding>(@LayoutRes layout: Int) :
    BaseBindingFragment<DB>(layout), IVideoCutterContext, IEventHandler {

    val mainActivity by lazy { requireActivity() as MainActivity }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    override fun onEvent(event: IEvent) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideLoading()
    }

    override fun showCustomToast(
        msg: CharSequence?,
        type: HEADER_ALERT_TYPE,
        timeShown: HEADER_ALERT_TIME_SHOWN,
        icon: Drawable?,
        bgColor: Int?
    ) {
        mainActivity.showCustomToast(msg, type, timeShown, icon, bgColor)
    }

    override fun showMessage(msg: String?, timeShown: HEADER_ALERT_TIME_SHOWN) {
        mainActivity.showMessage(msg, timeShown)
    }

    override fun showSuccess(msg: CharSequence?, timeShown: HEADER_ALERT_TIME_SHOWN) {
        mainActivity.showSuccess(msg, timeShown)
    }

    override fun showError(msg: String?, timeShown: HEADER_ALERT_TIME_SHOWN) {
       mainActivity.showError(msg, timeShown)
    }

    override fun showWarning(msg: String?, timeShown: HEADER_ALERT_TIME_SHOWN) {
        mainActivity.showWarning(msg, timeShown)
    }

    override fun showLoading(message: String) {
        mainActivity.showLoading(message)
    }

    override fun hideLoading() {
        mainActivity.hideLoading()
    }

    override fun onBackPressedFragment(tag: String?) {
       backScreen()
    }


    /**
     * chuyển đến 1 màn hình khác
     *
     * @param resId
     *          - R.id.<id của fragment trong nav graph>
     * @param bundle :
     *          - truyền data cho fragment nhận,
     *          - code được define const val ở trong fragment muốn nhận
     * @param navOptions
     *          - thêm các tuỳ chọn
     */
    fun navigateTo(
        @IdRes resId: Int,
        args: Bundle? = null,
        navOptions: NavOptions? = getUpNavOptions()
    ) {
        try {
            findNavController().navigate(resId, args, navOptions)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "navigateTo: $e")
        }
    }

    /**
     * trở lại màn hình trước đó
     *
     * @param resId
     *          - R.id.<id của fragment trong nav graph>
     * @param inclusive
     *          - keep back stack
     */
    fun popBackStack(resId: Int, inclusive: Boolean = false) {
        try {
            findNavController().popBackStack(resId, inclusive)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            Log.e(TAG, "popBackStack: $e")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "popBackStack: $e")
        }
    }

    /**
     * trở lại màn hình trước đó
     */
    private fun backScreen() {
        try {
            findNavController().navigateUp()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            Log.e(TAG, "backScreen: $e")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "backScreen: $e")
        }
    }
}
