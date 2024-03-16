package com.example.videocutter.di

import com.example.videocutter.presentation.display.IRepoDisplay
import com.example.videocutter.presentation.display.repo.RepoDisplayImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoDisplayModule {
    @Binds
    abstract fun bindRepoDisplay(
        repoDisplayImpl: RepoDisplayImpl
    ): IRepoDisplay
}
