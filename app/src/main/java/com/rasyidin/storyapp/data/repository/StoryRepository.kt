package com.rasyidin.storyapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import com.rasyidin.storyapp.data.model.Story
import com.rasyidin.storyapp.data.source.local.entity.StoryEntity
import com.rasyidin.storyapp.data.source.remote.response.ApiResponse
import com.rasyidin.storyapp.data.utils.ResultState
import kotlinx.coroutines.flow.Flow
import java.io.File

interface StoryRepository {

    suspend fun postStory(description: String, imageFile: File): Flow<ResultState<ApiResponse>>

    suspend fun postStory(description: String, imageFile: File, latitude: Double, longitude: Double): Flow<ResultState<ApiResponse>>

    suspend fun getStories(page: Int = 1, size: Int = 15, withLocation: Int = 1): Flow<ResultState<List<Story>>>

    suspend fun getStories(): Flow<ResultState<Pager<Int, StoryEntity>>>
}