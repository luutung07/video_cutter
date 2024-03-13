package com.example.videocutter.di

import com.example.videocutter.common.loader.image.ILoadImage
import com.example.videocutter.common.loader.image.LoadImageImpl
import com.example.videocutter.data.repo.MusicRepoImpl
import com.example.videocutter.data.repo.VideoRepoImpl
import com.example.videocutter.domain.repo.IMusicRepo
import com.example.videocutter.domain.repo.IVideoRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {
    @Binds
    abstract fun bindVideoRepo(
        videoRepoImpl: VideoRepoImpl
    ): IVideoRepo

    @Binds
    abstract fun bindMusicRepo(
        musicRepoImpl: MusicRepoImpl
    ): IMusicRepo
}
