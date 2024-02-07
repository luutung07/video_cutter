package com.example.videocutter.common.event

import com.example.library_base.eventbus.IEvent
import com.example.videocutter.domain.model.VideoInfo

class SelectFolderEvent(val id: Long?, val name: String) : IEvent

class DeleteVideoEvent(val list: List<VideoInfo>?) : IEvent
