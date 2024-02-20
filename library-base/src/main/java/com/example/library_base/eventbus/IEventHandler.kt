package com.example.library_base.eventbus

import com.example.library_base.eventbus.IEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

interface IEventHandler {
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onEvent(event: IEvent)
}
