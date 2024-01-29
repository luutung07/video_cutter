package com.example.videocutter.common.extensions

import androidx.annotation.IdRes
import androidx.navigation.NavOptions
import com.example.videocutter.R

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
