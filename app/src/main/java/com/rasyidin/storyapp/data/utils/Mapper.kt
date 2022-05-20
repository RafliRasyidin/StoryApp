package com.rasyidin.storyapp.data.utils

import com.rasyidin.storyapp.data.model.Login
import com.rasyidin.storyapp.data.model.Story
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