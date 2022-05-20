package com.rasyidin.storyapp.di

import android.content.Context
import com.rasyidin.storyapp.data.source.local.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun providesSessionManager(@ApplicationContext context: Context) = SessionManager(context)
}