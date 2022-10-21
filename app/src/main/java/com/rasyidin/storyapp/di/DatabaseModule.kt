package com.rasyidin.storyapp.di

import android.content.Context
import androidx.room.Room
import com.rasyidin.storyapp.data.source.local.SessionManager
import com.rasyidin.storyapp.data.source.local.db.StoryDatabase
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

    @Provides
    @Singleton
    fun providesStoryDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, StoryDatabase::class.java, "story_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun providesStoryDao(database: StoryDatabase) = database.storyDao()

    @Provides
    @Singleton
    fun providesRemoteKeyDao(database: StoryDatabase) = database.remoteKeyDao()
}