package com.example.videocutter.common.srceen

import android.graphics.drawable.Drawable
import com.example.baseapp.base.extension.getAppString
import com.example.videocutter.R
import com.example.videocutter.presentation.widget.headeralert.HEADER_ALERT_TIME_SHOWN
import com.example.videocutter.presentation.widget.headeralert.HEADER_ALERT_TYPE

interface IVideoCutterContext {
    fun showLoading(message: String = getAppString(R.string.loading)) {}
    fun hideLoading() {}
    fun showCustomToast(
        msg: CharSequence?,
        type: HEADER_ALERT_TYPE = HEADER_ALERT_TYPE.CUSTOM,
        timeShown: HEADER_ALERT_TIME_SHOWN = HEADER_ALERT_TIME_SHOWN.DELAY_DEFAULT,
        icon: Drawable? = null,
        bgColor: Int? = null
    )

    fun showMessage(
        msg: String?,
        timeShown: HEADER_ALERT_TIME_SHOWN = HEADER_ALERT_TIME_SHOWN.DELAY_DEFAULT
    )

    fun showSuccess(
        msg: CharSequence?,
        timeShown: HEADER_ALERT_TIME_SHOWN = HEADER_ALERT_TIME_SHOWN.DELAY_DEFAULT
    )

    fun showError(
        msg: String?,
        timeShown: HEADER_ALERT_TIME_SHOWN = HEADER_ALERT_TIME_SHOWN.DELAY_DEFAULT
    )

    fun showWarning(
        msg: String?,
        timeShown: HEADER_ALERT_TIME_SHOWN = HEADER_ALERT_TIME_SHOWN.DELAY_DEFAULT
    )
}
