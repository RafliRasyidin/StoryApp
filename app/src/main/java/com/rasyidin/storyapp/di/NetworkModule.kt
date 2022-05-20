package com.rasyidin.storyapp.di

import com.rasyidin.storyapp.BuildConfig
import com.rasyidin.storyapp.data.source.local.SessionManager
import com.rasyidin.storyapp.data.source.remote.network.AuthService
import com.rasyidin.storyapp.data.source.remote.network.StoryService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    companion object {
        const val BASE_URL = "https://story-api.dicoding.dev/v1/"
        const val AUTH = "Authorization"
    }

    private fun loggingInterceptor(): HttpLoggingInterceptor {
        return if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }
    }

    @Provides
    @Singleton
    fun providesHttpClient(sessionManager: SessionManager): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor())
            .addInterceptor { chain ->
                val token = runBlocking {
                    sessionManager.getToken().first()
                }
                chain.proceed(
                    chain.request()
                        .newBuilder()
                        .addHeader(AUTH, "Bearer $token")
                        .build()
                )
            }
            .build()

    @Provides
    @Singleton
    fun providesAuthService(okHttpClient: OkHttpClient): AuthService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
        .create(AuthService::class.java)

    @Provides
    @Singleton
    fun providesStoryService(okHttpClient: OkHttpClient): StoryService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
        .create(StoryService::class.java)
}