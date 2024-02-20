package com.example.library_base.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.example.baseapp.base.extension.runOnMainThread

object ToastUtils {
    private var toast: Toast? = null
    fun show(context: Context, msg: String?, time: Int) {
        toast?.cancel()
        toast = Toast.makeText(context, msg, time)
        context.runOnMainThread({
            toast?.show()
        })
    }

    fun show(context: Context, @StringRes resId: Int, time: Int) {
        toast?.cancel()
        toast = Toast.makeText(context, resId, time)
        toast?.show()
    }

    fun hide() {
        toast?.cancel()
    }
}
