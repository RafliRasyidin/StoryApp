package com.rasyidin.storyapp.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story")
data class StoryEntity(
    @ColumnInfo("photo_url")
    val photoUrl: String? = null,

    @ColumnInfo("created_at")
    val createdAt: String? = null,

    @ColumnInfo("name")
    val name: String? = null,

    @ColumnInfo("description")
    val description: String? = null,

    @ColumnInfo("longitude")
    val lng: Double? = null,

    @PrimaryKey
    val id: String,

    @ColumnInfo("latitude")
    val lat: Double? = null
)
