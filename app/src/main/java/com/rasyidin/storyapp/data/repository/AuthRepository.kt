package com.rasyidin.storyapp.data.repository

import com.rasyidin.storyapp.data.model.Login
import com.rasyidin.storyapp.data.source.remote.response.ApiResponse
import com.rasyidin.storyapp.data.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun register(name: String, email: String, password: String): Flow<ResultState<ApiResponse>>

    suspend fun login(email: String, password: String): Flow<ResultState<Login>>

    suspend fun saveToken(token: String)

    suspend fun setLoginState(state: Boolean)

    suspend fun setOnBoardingState(state: Boolean)

    suspend fun removeToken()

    fun getToken(): Flow<String>

    fun getLoginState(): Flow<Boolean>

    fun getOnBoardingState(): Flow<Boolean>
}