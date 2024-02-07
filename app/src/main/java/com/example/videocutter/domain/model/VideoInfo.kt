package com.example.videocutter.domain.model

import android.os.Parcelable
import com.example.baseapp.base.extension.getAppString
import com.example.videocutter.R
import com.example.videocutter.common.extensions.convertTimeToString
import kotlinx.parcelize.Parcelize

@Parcelize
class VideoInfo(
    var id: Long? = null,

    var name: String? = null,

    var thumbnailUrl: String? = null,

    var createTime: String? = null,

    var duration: Long? = null,

    var count: Int? = null
) : Parcelable {
    fun getTime(): String {
        if (duration == null) return getAppString(R.string.time_default)
        return duration!!.convertTimeToString()
    }
}
