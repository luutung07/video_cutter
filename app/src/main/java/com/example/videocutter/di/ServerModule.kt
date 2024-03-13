package com.example.videocutter.di

import com.example.videocutter.data.remote.server.ITunesService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServerModule {
    companion object {
        private const val BASE_URL = "https://itunes.apple.com/"
    }

    @Provides
    @Singleton
    fun providerBaseUrl(): String = BASE_URL

    @Provides
    @Singleton
    fun providerOkhttp(): OkHttpClient {
        val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
    }

    @Provides
    @Singleton
    fun providerITunesServer(okHttpClient: OkHttpClient, baseUrl: String): ITunesService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ITunesService::class.java)
    }
}
