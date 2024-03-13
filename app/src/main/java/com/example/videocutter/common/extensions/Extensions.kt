package com.example.videocutter.common.extensions

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import com.example.baseapp.base.extension.getAppString
import com.example.library_base.common.DataPage
import com.example.library_base.common.usecase.FlowResult
import com.example.library_base.common.usecase.IViewListener
import com.example.library_base.common.usecase.UI_STATE
import com.example.videocutter.R
import com.example.videocutter.common.loader.LoadImageFactory
import com.example.videocutter.common.srceen.VideoCutterActivity
import com.example.videocutter.common.srceen.VideoCutterFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun getNavOptionWithoutAnim(
    @IdRes resId: Int? = null,
    inclusive: Boolean = false,
): NavOptions {
    val navOptions = NavOptions.Builder()
    resId?.let {
        navOptions.setPopUpTo(resId, inclusive)
    }
    return navOptions.build()
}

fun getUpNavOptions(
    @IdRes resId: Int? = null,
    inclusive: Boolean = false,
    isBackToPrevious: Boolean = false
): NavOptions {
    val navOptions = if (isBackToPrevious) {
        NavOptions.Builder()
            .setEnterAnim(com.example.library_base.R.anim.slide_pop_enter_right_to_left)
            .setExitAnim(com.example.library_base.R.anim.slide_pop_exit_left_to_right)
            .setPopEnterAnim(com.example.library_base.R.anim.slide_enter_left_to_right)
            .setPopExitAnim(com.example.library_base.R.anim.slide_exit_right_to_left)
    } else {
        NavOptions.Builder()
            .setEnterAnim(com.example.library_base.R.anim.slide_enter_left_to_right)
            .setExitAnim(com.example.library_base.R.anim.slide_exit_right_to_left)
            .setPopEnterAnim(com.example.library_base.R.anim.slide_pop_enter_right_to_left)
            .setPopExitAnim(com.example.library_base.R.anim.slide_pop_exit_left_to_right)
    }

    resId?.let {
        navOptions.setPopUpTo(resId, inclusive)
    }

    return navOptions.build()
}


fun ImageView.loadImage(url: String) {
    LoadImageFactory.getLoadImage().loadImage(view = this, url = url)
}

fun ImageView.loadImage(drawable: Drawable?) {
    LoadImageFactory.getLoadImage().loadImage(view = this, drawable = drawable)
}

fun <DATA> Fragment.coroutinesLaunch(
    flow: Flow<FlowResult<DATA>>,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    launch: suspend (flowResult: FlowResult<DATA>) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(state = state) {
            flow.collect {
                launch.invoke(it)
            }
        }
    }
}

fun <DATA> AppCompatActivity.coroutinesLaunch(
    flow: Flow<FlowResult<DATA>>,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    launch: suspend (flowResult: FlowResult<DATA>) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(state = state) {
            flow.collect {
                launch.invoke(it)
            }
        }
    }
}

fun <T> VideoCutterActivity<*>.handleUiState(
    flowResult: FlowResult<T>,
    listener: IViewListener? = null,
    canShowLoading: Boolean = false,
    canHideLoading: Boolean = false,
    canShowError: Boolean = true,
) {
    when (flowResult.getUiState()) {
        UI_STATE.INITIAL -> {
            listener?.onInitial()
        }

        UI_STATE.LOADING -> {
            if (canShowLoading) {
                showLoading()
            }
            listener?.onLoading()
        }

        UI_STATE.FAILURE -> {
            hideLoading()
            listener?.onFailure()
            if (canShowError) {
                showError(flowResult.getMessage())
            }
        }

        UI_STATE.SUCCESS -> {
            if (canHideLoading) {
                hideLoading()
            }
            listener?.onSuccess()
        }

        else -> {}
    }
}

fun <T> VideoCutterFragment<*>.handleUiState(
    flowResult: FlowResult<T>,
    listener: IViewListener? = null,
    canShowLoading: Boolean = false,
    canHideLoading: Boolean = false,
    canShowError: Boolean = true,
    handleBlock: Boolean = true
) {
    when (flowResult.getUiState()) {
        UI_STATE.INITIAL -> {
            listener?.onInitial()
        }

        UI_STATE.LOADING -> {
            if (canShowLoading) {
                showLoading()
            }
            listener?.onLoading()
        }

        UI_STATE.FAILURE -> {
            hideLoading()
            listener?.onFailure()
            if (canShowError) {
                showError(flowResult.getMessage())
            }
        }

        UI_STATE.SUCCESS -> {
            if (canHideLoading) {
                hideLoading()
            }
            listener?.onSuccess()
        }

        else -> {}
    }
}

fun <T> getDataPage(dataPage: DataPage<T>?, isReload: Boolean = true): DataPage<T> {
    var _dataPage = dataPage
    if (_dataPage == null) {
        _dataPage = DataPage()
    } else {
        if (isReload) {
            _dataPage.reset()
        }
    }
    return _dataPage
}

fun Long.convertTimeToString(): String {
    val seconds: Long = this / 1000 % 60 // Extract seconds from milliseconds
    val minutes: Long = this / (1000 * 60) % 60 // Extract minutes from milliseconds
    return String.format("%02d:%02d", minutes, seconds)
}

fun View.getCoordinateXView(): Int {
    val location = IntArray(2)
    this.getLocationOnScreen(location)
    return location[0]
}

fun View.getCoordinateYView(): Int {
    val location = IntArray(2)
    this.getLocationInWindow(location)
    return location[1]
}
