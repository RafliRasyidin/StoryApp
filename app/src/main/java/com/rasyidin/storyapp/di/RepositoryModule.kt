package com.rasyidin.storyapp.di

import com.rasyidin.storyapp.data.repository.AuthRepository
import com.rasyidin.storyapp.data.repository.AuthRepositoryImp
import com.rasyidin.storyapp.data.repository.StoryRepository
import com.rasyidin.storyapp.data.repository.StoryRepositoryImp
import com.rasyidin.storyapp.data.source.local.LocalDataSource
import com.rasyidin.storyapp.data.source.local.SessionManager
import com.rasyidin.storyapp.data.source.remote.network.AuthService
import com.rasyidin.storyapp.data.source.remote.network.StoryService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun providesAuthRepository(service: AuthService, local: LocalDataSource): AuthRepository {
        return AuthRepositoryImp(service, local)
    }

    @Provides
    @Singleton
    fun providesStoryRepository(service: StoryService): StoryRepository {
        return StoryRepositoryImp(service)
    }

    @Provides
    @Singleton
    fun providesLocalDataSource(sessionManager: SessionManager): LocalDataSource {
        return LocalDataSource(sessionManager)
    }
}