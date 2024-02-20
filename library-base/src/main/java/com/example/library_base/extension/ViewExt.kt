package com.example.baseapp.base.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Insets
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.SpannableString
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsets
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.library_base.R
import com.example.library_base.common.BaseActivity
import com.example.library_base.common.BaseFragment
import com.example.library_base.common.DebouncedOnClickListener
import com.example.library_base.common.GlobalDebouncedOnClickListener
import com.example.library_base.utils.ToastUtils
import com.example.library_base.extension.getApplication

const val DEFAULT_DEBOUNCE_INTERVAL = 350L

fun View.setOnSafeClick(
    delayBetweenClicks: Long = DEFAULT_DEBOUNCE_INTERVAL,
    click: (view: View) -> Unit
) {
    setOnClickListener(object : DebouncedOnClickListener(delayBetweenClicks) {
        override fun onDebouncedClick(v: View) = click(v)
    })
}

fun View.setOnSafeGlobalClick(
    delayBetweenClicks: Long = DEFAULT_DEBOUNCE_INTERVAL,
    click: (view: View) -> Unit
) {
    setOnClickListener(object : GlobalDebouncedOnClickListener(delayBetweenClicks) {
        override fun onDebouncedClick(v: View) = click(v)
    })
}

fun View.setOnScaleClick(action: (() -> Unit)) {
    setOnSafeClick {
        scaleAnimation()
        action()
    }
}

fun View.scaleAnimation() {
    animate()
        .scaleX(1.3f)
        .scaleY(1.3f)
        .setDuration(150)
        .setInterpolator(DecelerateInterpolator())
        .withEndAction {
            ViewCompat.animate(this)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(50).interpolator = AccelerateInterpolator()
        }
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

fun TextView.setImageLeft(left: Drawable?) {
    setCompoundDrawablesWithIntrinsicBounds(left, null, null, null)
}

fun TextView.setImageTop(top: Drawable?) {
    setCompoundDrawablesWithIntrinsicBounds(null, top, null, null)
}

fun TextView.setImageRight(right: Drawable?) {
    setCompoundDrawablesWithIntrinsicBounds(null, null, right, null)
}

fun TextView.setImageBottom(bottom: Drawable?) {
    setCompoundDrawablesWithIntrinsicBounds(null, null, null, bottom)
}

fun TextView.setImageLeftRight(left: Drawable?, right: Drawable?) {
    setCompoundDrawablesWithIntrinsicBounds(left, null, right, null)
}

fun TextView.clearImage() {
    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
}

fun getAppString(
    @StringRes stringId: Int,
    context: Context? = getApplication()
): String {
    return context?.getString(stringId) ?: ""
}

fun getAppString(
    @StringRes stringId: Int,
    vararg params: Any,
    context: Context? = getApplication()
): String {
    return context?.getString(stringId, *params) ?: ""
}

fun getAppSpannableString(
    @StringRes stringId: Int,
    context: Context? = getApplication()
): SpannableString {
    return SpannableString(context?.getString(stringId))
}

fun getAppFont(
    @FontRes fontId: Int,
    context: Context? = getApplication()
): Typeface? {
    return context?.let {
        ResourcesCompat.getFont(it, fontId)
    }
}

fun getAppDrawable(
    @DrawableRes drawableId: Int,
    context: Context? = getApplication()
): Drawable? {
    return context?.let {
        ContextCompat.getDrawable(it, drawableId)
    }
}

fun getAppDimensionPixel(
    @DimenRes dimenId: Int,
    context: Context? = getApplication()
): Int {
    return context?.resources?.getDimensionPixelSize(dimenId) ?: -1
}

fun getAppDimension(
    @DimenRes dimenId: Int,
    context: Context? = getApplication()
): Float {
    return context?.resources?.getDimension(dimenId) ?: -1f
}

fun getAppColor(
    @ColorRes colorRes: Int,
    context: Context? = getApplication()
): Int {
    return context?.let {
        ContextCompat.getColor(it, colorRes)
    } ?: Color.TRANSPARENT
}

fun Activity.getScreenHeight(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = windowManager.currentWindowMetrics
        val insets: Insets = windowMetrics.windowInsets
            .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        windowMetrics.bounds.height() - insets.top - insets.bottom
    } else {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.heightPixels
    }
}

fun Any.runOnMainThread(task: () -> Any?, delayMs: Long = 0L) {
    Handler(Looper.getMainLooper()).postDelayed({
        when (this) {
            is BaseActivity -> {
                runIfNotDestroyed { task() }
            }
            is BaseFragment -> {
                runIfNotDestroyed { task() }
            }
            else -> {
                task()
            }
        }
    }, delayMs)
}

fun BaseActivity.runIfNotDestroyed(task: () -> Any?) {
    if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            !isDestroyed
        } else {
            true
        }
    ) {
        task()
    }
}

fun BaseFragment.runIfNotDestroyed(task: () -> Any?) {
    if (this.lifecycle.currentState != Lifecycle.State.DESTROYED) {
        task()
    }
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelableArrayList(key: String): ArrayList<T>? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableArrayList(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayList(key)
}

inline fun <reified T : Parcelable> Intent.parcelableArrayList(key: String): ArrayList<T>? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableArrayListExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayListExtra(key)
}

fun Context.toast(msg: String?) {
    ToastUtils.show(this, msg, Toast.LENGTH_SHORT)
}

fun AppCompatActivity.toast(msg: String?) {
    ToastUtils.show(this, msg, Toast.LENGTH_SHORT)
}

fun Context.hideToast() {
    ToastUtils.hide()
}

fun Fragment.toast(msg: String?) {
    context?.let {
        ToastUtils.show(it, msg, Toast.LENGTH_SHORT)
    }
}

private var lastPress: Long = 0

private const val EXIT_APP_DELAY = 1500

fun Context.checkBeforeBack(action: () -> Unit) {
    val currentTime = System.currentTimeMillis()
    if (currentTime - lastPress > EXIT_APP_DELAY) {
        toast(getAppString(R.string.main_exit_confirm))
    } else {
        hideToast()
        action.invoke()
    }
    lastPress = currentTime
}


fun AppCompatActivity.checkBeforeBack(action: () -> Unit) {
    val currentTime = System.currentTimeMillis()
    if (currentTime - lastPress > EXIT_APP_DELAY) {
        toast(getAppString(R.string.main_exit_confirm))
    } else {
        hideToast()
        action.invoke()
    }
    lastPress = currentTime
}

fun Fragment.checkBeforeBack(action: () -> Unit) {
    val currentTime = System.currentTimeMillis()
    if (currentTime - lastPress > EXIT_APP_DELAY) {
        toast(getAppString(R.string.main_exit_confirm))
    } else {
        action.invoke()
    }
    lastPress = currentTime
}
