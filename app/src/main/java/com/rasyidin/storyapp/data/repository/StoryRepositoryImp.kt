package com.rasyidin.storyapp.data.repository

import androidx.paging.*
import com.rasyidin.storyapp.data.model.Story
import com.rasyidin.storyapp.data.source.local.StoryRemoteMediator
import com.rasyidin.storyapp.data.source.local.db.StoryDatabase
import com.rasyidin.storyapp.data.source.local.entity.StoryEntity
import com.rasyidin.storyapp.data.source.remote.network.StoryService
import com.rasyidin.storyapp.data.source.remote.response.ApiResponse
import com.rasyidin.storyapp.data.utils.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class StoryRepositoryImp @Inject constructor(
    private val service: StoryService,
    private val database: StoryDatabase
) : StoryRepository {

    override suspend fun postStory(
        description: String,
        imageFile: File
    ): Flow<ResultState<ApiResponse>> {
        return fetch {
            val descRequestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpg".toMediaTypeOrNull())
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

    override suspend fun postStory(
        description: String,
        imageFile: File,
        latitude: Double,
        longitude: Double
    ): Flow<ResultState<ApiResponse>> {
        return fetch {
            val descRequestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val latRequestBody = latitude.toString().toRequestBody("text/plain".toMediaType())
            val lonRequestBody = longitude.toString().toRequestBody("text/plain".toMediaType())
            val imageMultipart = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            service.postStory(descRequestBody, imageMultipart, latRequestBody, lonRequestBody)
        }.map { resultState ->
            mapResult(resultState) {
                this?.body()
            }
        }
    }

    override suspend fun getStories(
        page: Int,
        size: Int,
        withLocation: Int
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

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getStories(): Flow<ResultState<Pager<Int, StoryEntity>>> {
        return fetchPaging {
            Pager(
                config = PagingConfig(pageSize = 5),
                remoteMediator = StoryRemoteMediator(apiService = service, database = database),
                pagingSourceFactory = {
                    database.storyDao().getStories()
                }
            )
        }
    }
}