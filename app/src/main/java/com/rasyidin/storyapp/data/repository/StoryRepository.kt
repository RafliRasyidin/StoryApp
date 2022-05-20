package com.rasyidin.storyapp.data.repository

import com.rasyidin.storyapp.data.model.Story
import com.rasyidin.storyapp.data.source.remote.response.ApiResponse
import com.rasyidin.storyapp.data.utils.ResultState
import kotlinx.coroutines.flow.Flow
import java.io.File

interface StoryRepository {

    suspend fun postStory(description: String, imageFile: File): Flow<ResultState<ApiResponse>>

    suspend fun getStories(page: Int = 1, size: Int = 15, withLocation: Boolean = false): Flow<ResultState<List<Story>>>
}