package com.rasyidin.storyapp.data.source.remote.network

import com.rasyidin.storyapp.data.source.remote.response.ApiResponse
import com.rasyidin.storyapp.data.source.remote.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface StoryService {

    @POST("stories")
    @Multipart
    suspend fun postStory(
        @Part("description") description: RequestBody,
        @Part imageFile: MultipartBody.Part
    ): Response<ApiResponse>

    @POST("stories")
    @Multipart
    suspend fun postStory(
        @Part("description") description: RequestBody,
        @Part imageFile: MultipartBody.Part,
        @Part("lat") latitude: RequestBody? = null,
        @Part("lon") longitude: RequestBody? = null
    ): Response<ApiResponse>

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 15,
        @Query("location") withLocation: Int = 1
    ): Response<StoriesResponse>
}