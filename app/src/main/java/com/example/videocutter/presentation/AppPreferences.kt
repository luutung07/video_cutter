package com.example.videocutter.presentation

import android.content.Context
import android.content.SharedPreferences
import com.example.library_base.extension.INT_DEFAULT
import com.google.gson.Gson

object AppPreferences {
    private lateinit var preferences: SharedPreferences

    private const val MODE = Context.MODE_PRIVATE
    private lateinit var gson: Gson

    private const val QUALITY_VIDEO_KEY = "QUALITY_VIDEO_KEY"

    fun init(context: Context) {
        preferences = context.getSharedPreferences(context.packageName, MODE)
        gson = Gson()
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    private inline fun SharedPreferences.commit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.commit()
    }

    var qualityValueVideo: Int?
        get() = preferences.getInt(QUALITY_VIDEO_KEY, -1)
        set(value) = preferences.edit {
            value?.let { it1 -> it.putInt(QUALITY_VIDEO_KEY, it1) }
        }


}
