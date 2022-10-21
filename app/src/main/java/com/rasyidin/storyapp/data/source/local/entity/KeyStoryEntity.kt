package com.rasyidin.storyapp.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("key_story")
data class KeyStoryEntity(
    @PrimaryKey
    @ColumnInfo("id_key")
    val id: String,

    @ColumnInfo("prev_key")
    val prevKey: Int?,

    @ColumnInfo("next_key")
    val nextKey: Int?

)
