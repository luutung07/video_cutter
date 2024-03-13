package com.example.videocutter.data.remote.server

import com.example.videocutter.data.remote.model.ITunesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesService {
    @GET("search")
    fun getItunes(
        @Query("term") term: String = "jack+johnson",
        @Query("entity") entity: String = "musicVideo"
    ): Call<ITunesResponse>
}
