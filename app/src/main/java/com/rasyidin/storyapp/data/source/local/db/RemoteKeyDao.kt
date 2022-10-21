package com.rasyidin.storyapp.data.source.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rasyidin.storyapp.data.source.local.entity.KeyStoryEntity

@Dao
interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<KeyStoryEntity>)

    @Query("SELECT * FROM key_story WHERE id_key = :id")
    suspend fun getRemoteKeyId(id: String): KeyStoryEntity?

    @Query("DELETE FROM key_story")
    suspend fun deleteRemoteKeys()
}