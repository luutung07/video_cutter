package com.example.videocutter.presentation.display.model.video

enum class QUALITY_VIDEO_TYPE(val value: Int) {
    LOW(0), MEDIUM(1), HIGH(2);

    companion object {
        fun getItem(value: Int?): QUALITY_VIDEO_TYPE? {
            val item = values().find {
                it.value == value
            }
            return item
        }
    }
}
