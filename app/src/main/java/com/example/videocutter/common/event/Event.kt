package com.example.videocutter.common.event

import com.example.library_base.eventbus.IEvent

class SelectFolderEvent(val id: Long?, val name: String) : IEvent
