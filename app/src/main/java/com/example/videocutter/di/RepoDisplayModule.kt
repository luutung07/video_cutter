package com.example.videocutter.di

import com.example.videocutter.presentation.repodisplay.IRepoDisplay
import com.example.videocutter.presentation.repodisplay.repo.RepoDisplayImpl
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
