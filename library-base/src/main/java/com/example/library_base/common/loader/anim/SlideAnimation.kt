package com.example.library_base.common.loader.anim

import com.example.library_base.common.navigation.IScreenAnim
import com.example.library_base.R

class SlideAnimation : IScreenAnim {
    override fun enter() = R.anim.slide_enter_bottom_to_top
    override fun exit() = R.anim.slide_exit_top_to_bottom
    override fun popEnter() = R.anim.slide_pop_enter_top_to_bottom
    override fun popExit() = R.anim.slide_pop_exit_bottom_to_top
}
