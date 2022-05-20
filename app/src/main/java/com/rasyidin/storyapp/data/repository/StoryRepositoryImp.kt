package com.rasyidin.storyapp.data.repository

import com.rasyidin.storyapp.data.model.Story
import com.rasyidin.storyapp.data.source.remote.network.StoryService
import com.rasyidin.storyapp.data.source.remote.response.ApiResponse
import com.rasyidin.storyapp.data.utils.ResultState
import com.rasyidin.storyapp.data.utils.fetch
import com.rasyidin.storyapp.data.utils.mapResult
import com.rasyidin.storyapp.data.utils.toStory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class StoryRepositoryImp @Inject constructor(private val service: StoryService): StoryRepository {

    override suspend fun postStory(
        description: String,
        imageFile: File
    ): Flow<ResultState<ApiResponse>> {
        return fetch {
            val descRequestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            service.postStory(descRequestBody, imageMultipart)
        }.map { resultState ->
            mapResult(resultState) {
                this?.body()
            }
        }
    }

    override suspend fun getStories(
        page: Int,
        size: Int,
        withLocation: Boolean
    ): Flow<ResultState<List<Story>>> {
        return fetch {
            service.getStories(page, size, withLocation)
        }.map { resultState ->
            mapResult(resultState) {
                this?.body()?.stories?.map { storyResponse ->
                    storyResponse.toStory()
                }
            }
        }
    }
}