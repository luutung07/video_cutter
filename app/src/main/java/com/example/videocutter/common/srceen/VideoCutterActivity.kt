package com.example.videocutter.common.srceen

import android.graphics.drawable.Drawable
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.example.library_base.common.binding.BaseBindingActivity
import com.example.library_base.eventbus.IEvent
import com.example.library_base.eventbus.IEventHandler
import com.example.videocutter.presentation.widget.headeralert.HEADER_ALERT_TIME_SHOWN
import com.example.videocutter.presentation.widget.headeralert.HEADER_ALERT_TYPE
import com.example.videocutter.presentation.widget.headeralert.HeaderAlertDefault
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

open class VideoCutterActivity<DB : ViewDataBinding>(@LayoutRes layoutRes: Int) :
    BaseBindingActivity<DB>(layoutRes), IEventHandler, IVideoCutterContext {

    private var headerAlert: HeaderAlertDefault? = null

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    override fun onEvent(event: IEvent) {

    }

    override fun showCustomToast(
        msg: CharSequence?,
        type: HEADER_ALERT_TYPE,
        timeShown: HEADER_ALERT_TIME_SHOWN,
        icon: Drawable?,
        bgColor: Int?
    ) {
        if (headerAlert == null) {
            headerAlert = HeaderAlertDefault(this)
        }
        headerAlert?.timeShown = timeShown
        if (type == HEADER_ALERT_TYPE.CUSTOM && icon != null && bgColor != null) {
            headerAlert?.icon = icon
            headerAlert?.bgColor = bgColor
        }
        headerAlert?.show(msg, type)
    }

    override fun showMessage(msg: String?, timeShown: HEADER_ALERT_TIME_SHOWN) {
        showCustomToast(
            msg = msg,
            type = HEADER_ALERT_TYPE.CUSTOM,
        )
    }

    override fun showSuccess(msg: CharSequence?, timeShown: HEADER_ALERT_TIME_SHOWN) {
        showCustomToast(
            msg = msg,
            type = HEADER_ALERT_TYPE.SUCCESS,
        )
    }

    override fun showError(msg: String?, timeShown: HEADER_ALERT_TIME_SHOWN) {
        showCustomToast(
            msg = msg,
            type = HEADER_ALERT_TYPE.ERROR,
        )
    }

    override fun showWarning(msg: String?, timeShown: HEADER_ALERT_TIME_SHOWN) {
        showCustomToast(
            msg = msg,
            type = HEADER_ALERT_TYPE.WARNING,
        )
    }
}
