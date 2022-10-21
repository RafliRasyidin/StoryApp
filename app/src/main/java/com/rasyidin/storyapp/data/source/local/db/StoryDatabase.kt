package com.rasyidin.storyapp.data.source.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rasyidin.storyapp.data.source.local.entity.KeyStoryEntity
import com.rasyidin.storyapp.data.source.local.entity.StoryEntity

@Database(
    entities = [KeyStoryEntity::class, StoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase: RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}