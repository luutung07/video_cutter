package com.example.videocutter.presentation.widget.speedvideo

enum class SPEED_TYPE(val value: Float) {
    SPEED_0_75(0.75f),
    SPEED_0_5(0.5f),
    SPEED_0_25(0.25f),
    SPEED_1(1f),
    SPEED_2(2f),
    SPEED_3(3f),
    SPEED_4(4f);

    companion object {
        fun getValues(value: Float): SPEED_TYPE {
            val item = values().find {
                it.value == value
            }
            return item?: throw NullPointerException("not find SPEED_TYPE")
        }
    }
}
