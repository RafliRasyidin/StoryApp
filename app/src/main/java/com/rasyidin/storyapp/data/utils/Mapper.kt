package com.rasyidin.storyapp.data.utils

import com.rasyidin.storyapp.data.model.Login
import com.rasyidin.storyapp.data.model.Story
import com.rasyidin.storyapp.data.source.local.entity.StoryEntity
import com.rasyidin.storyapp.data.source.remote.response.LoginResult
import com.rasyidin.storyapp.data.source.remote.response.StoryResponse

fun StoryResponse.toStory() = Story(
    photoUrl = this.photoUrl,
    createdAt = this.createdAt,
    name = this.name,
    description = this.description,
    lng = this.lng,
    id = this.id,
    lat = this.lat
)

fun LoginResult.toLogin() = Login(
    name = this.name,
    userId = this.userId,
    token = this.token
)

fun List<StoryResponse>.toListStoryEntity(): List<StoryEntity> {
    val storiesEntity = ArrayList<StoryEntity>()
    this.map {
        val storyEntity = StoryEntity(
            photoUrl = it.photoUrl,
            createdAt = it.createdAt,
            name = it.name,
            description = it.description,
            lng = it.lng,
            lat = it.lat,
            id = it.id.toString()
        )
        storiesEntity.add(storyEntity)
    }
    return storiesEntity
}

fun StoryEntity.toStory() = Story(
    photoUrl = this.photoUrl,
    createdAt = this.createdAt,
    name = this.name,
    description = this.description,
    lng = this.lng,
    id = this.id,
    lat = this.lat
)