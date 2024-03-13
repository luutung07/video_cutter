package com.example.videocutter.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ITunesResponse (
    @SerializedName("resultCount")
    @Expose
    var resultCount: Int? = null,

    @SerializedName("results")
    @Expose
    var results: List<ITunesDTO>? = null,
)
